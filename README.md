# 图书馆座位预约系统

一个面向高校图书馆的座位预约与管理系统：学生按时间段预约座位，系统保证**同一座位同一时段不会被两个人占用**，无人签到的预约自动释放。

---

## 技术栈

| 组件 | 版本 |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| MyBatis-Plus | 3.5.17 |
| MySQL | 5.7 |

---

## 核心设计：为什么用「固定时段 + 占位明细表」

### 问题

预约的本质是"占用一个时间段"，而时间段是**区间**。如果只用一张表存 `start_slot` / `end_slot`：

```sql
t_reservation
id | seat_id | reserve_date | start_slot | end_slot
1  | 3       | 2026-10-20   | 6          | 10        ← 占第 6~9 格
```

那么"这个座位这个时段有没有被占"就是一个**区间重叠**判断：

```sql
WHERE start_slot < 9 AND 8 < end_slot
```

**这带来三个致命问题：**

1. **用不上唯一索引。** 唯一索引擅长等值判断，对"区间是否相交"无能为力。
2. **数据库没法拒绝冲突的插入。** 对数据库来说 `[6,10)` 和 `[6,11)` 是两个不同的值。
3. **只能在应用层"先查再插"—— 而它在并发下必然失效。** 两个请求同时查到"空的"，然后都插入成功。

### 解决：把「区间」拆成「点」

**每一次预约占用的每一格，单独存一行：**

```sql
t_reservation_slot
reservation_id | seat_id | reserve_date | slot_no
1              | 3       | 2026-10-20   | 6
1              | 3       | 2026-10-20   | 7
1              | 3       | 2026-10-20   | 8
1              | 3       | 2026-10-20   | 9
```

**并在 `(seat_id, reserve_date, slot_no)` 上建唯一索引：**

```sql
UNIQUE KEY uk_seat_date_slot (seat_id, reserve_date, slot_no)
```

**于是：**

| 问题 | 谁来保证 |
|---|---|
| 两个请求同时抢同一格 | **数据库唯一索引，只有一个能成功** |
| 重复提交（手抖点两次） | **同样的格子已存在 → 唯一索引冲突 → 天然幂等** |
| 一次预约占 4 格，失败一半 | **事务：要么全占上，要么全回滚** |

> **一句话总结：把「区间是否相交」的问题，转化成「某个点是否已被占用」的问题。**
> **这样索引才能用上，数据库才能在物理层面拦住冲突，应用层不用写一行锁代码。**

### 边界约定：左闭右开 `[startSlot, endSlot)`

- `startSlot` / `endSlot` 是**刻度**（时刻），不是格子编号
- 占用的格子 = `startSlot, startSlot+1, ..., endSlot-1`
- **格子 `i` 属于该预约 ⟺ `startSlot <= i < endSlot`**

**好处：**

| 好处 | 说明 |
|---|---|
| 相邻预约不冲突 | `[8,12)` 和 `[12,16)` 刚好接上，不重叠也不留空 |
| 长度计算简单 | `endSlot - startSlot` = 格子数，不用 `+1` |
| 与编程惯例一致 | `for (i = 0; i < n; i++)`、`substring(start, end)` 都是这个套路 |

---

## 数据库设计（5 张表）

| 表 | 作用 | 关键索引 |
|---|---|---|
| `t_user` | 用户 | `uk_username` |
| `t_room` | 阅览室（开放时段 `open_slot` ~ `close_slot`） | — |
| `t_seat` | 座位 | `uk_room_seat (room_id, seat_no)` |
| `t_reservation` | 预约单（一次预约一条） | `idx_user_date`、`idx_seat_date` |
| `t_reservation_slot` | **占位明细（一格一行）** | **`uk_seat_date_slot`** |

**时间模型：** 一天切成固定时段，`08:00–22:00` 每 1 小时一格，共 **14 格**（编号 0~13），对应刻度 0~14。

---

## 接口

| 方法 | 路径 | 说明 | 状态 |
|---|---|---|---|
| GET | `/room/list` | 阅览室列表 | ✅ |
| GET | `/room/{id}` | 单个阅览室 | ✅ |
| GET | `/seat/list?roomId=` | 座位列表（可按阅览室筛选） | ✅ |
| GET | `/seat/{id}` | 单个座位 | ✅ |
| GET | `/seat/available?seatId=&date=` | 某座位某天的空闲时段 | ✅ |
| POST | `/reservation` | **提交预约（事务 + 唯一索引）** | ✅ |
| DELETE | `/reservation/{id}` | 取消预约（释放格子） | ⬜ 待做 |
| GET | `/reservation/my` | 我的预约 | ⬜ 待做 |
| POST | `/reservation/{id}/checkin` | 签到（状态机） | ⬜ 待做 |
| GET | `/stat/usage?date=` | 使用率统计（聚合查询） | ⬜ 待做 |
| — | 定时任务 | 超时未签到自动释放 | ⬜ 待做 |

---

## 快速开始

### 1. 建库建表

```sql
CREATE DATABASE IF NOT EXISTS library_seat DEFAULT CHARACTER SET utf8mb4;
```

然后执行 `docs/schema.sql`（或参照本文档的「数据库设计」章节）。

### 2. 配置数据库

修改 `src/main/resources/application.properties` 里的 `spring.datasource.*`。

### 3. 启动

```bash
./mvnw spring-boot:run
```

看到 `Tomcat started on port 8080` 即成功。

### 4. 测试

用 IntelliJ IDEA 打开 `src/test/test1.http`，逐个点绿色 ▶ 发送请求。

---

## 开发笔记

踩过的坑、设计取舍、测试记录，见 [docs/开发笔记.md](docs/开发笔记.md)。
