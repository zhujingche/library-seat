-- ============================================================
-- 图书馆座位预约系统 · 建库建表脚本
-- MySQL 5.7+
-- 用法：mysql -uroot -p < docs/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS library_seat DEFAULT CHARACTER SET utf8mb4;
USE library_seat;

-- ---------- ① 用户 ----------
CREATE TABLE IF NOT EXISTS t_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(100) NOT NULL COMMENT 'TODO: 改为存 BCrypt 哈希，不要明文',
    name        VARCHAR(50),
    role        VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT 'STUDENT / ADMIN',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

-- ---------- ② 阅览室 ----------
-- 时间模型：一天切成固定时段，每 1 小时一格。
-- open_slot=0 表示 08:00，close_slot=14 表示 22:00，共 14 格（编号 0~13）。
CREATE TABLE IF NOT EXISTS t_room (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL,
    location   VARCHAR(100),
    open_slot  INT NOT NULL DEFAULT 0  COMMENT '开放起始刻度',
    close_slot INT NOT NULL DEFAULT 14 COMMENT '开放结束刻度',
    status     TINYINT NOT NULL DEFAULT 1 COMMENT '1开放 0关闭'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅览室';

-- ---------- ③ 座位 ----------
CREATE TABLE IF NOT EXISTS t_seat (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT      NOT NULL,
    seat_no VARCHAR(20) NOT NULL,
    status  TINYINT     NOT NULL DEFAULT 1 COMMENT '1可用 0维修中',
    UNIQUE KEY uk_room_seat (room_id, seat_no),
    KEY idx_room (room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位';

-- ---------- ④ 预约单 ----------
-- start_slot / end_slot 是【刻度】（时刻），不是格子编号。
-- 占用的格子 = [start_slot, end_slot)
CREATE TABLE IF NOT EXISTS t_reservation (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT      NOT NULL,
    seat_id      BIGINT      NOT NULL,
    reserve_date DATE        NOT NULL,
    start_slot   INT         NOT NULL COMMENT '起始刻度',
    end_slot     INT         NOT NULL COMMENT '结束刻度（不含）',
    status       VARCHAR(20) NOT NULL DEFAULT 'RESERVED'
                 COMMENT 'RESERVED / CHECKED_IN / COMPLETED / CANCELLED / NO_SHOW',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_date (user_id, reserve_date),
    KEY idx_seat_date (seat_id, reserve_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约单';

-- ---------- ⑤ 占位明细（核心表）----------
-- 一次预约占 N 格 = N 行。
-- uk_seat_date_slot 是整个项目的命门：
--   · 并发抢座：两个请求同时插入同一格，数据库只有一个能成功
--   · 重复提交：同样的格子已存在 → 唯一索引冲突 → 天然幂等
CREATE TABLE IF NOT EXISTS t_reservation_slot (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    seat_id        BIGINT NOT NULL,
    reserve_date   DATE   NOT NULL,
    slot_no        INT    NOT NULL COMMENT '格子编号',
    UNIQUE KEY uk_seat_date_slot (seat_id, reserve_date, slot_no),
    KEY idx_reservation (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='占位明细';

-- ---------- 初始数据 ----------
INSERT INTO t_user (username, password, name, role) VALUES
    ('admin', 'admin123', '管理员', 'ADMIN'),
    ('zjc',   '123456',   '景澈',   'STUDENT');

INSERT INTO t_room (name, location, open_slot, close_slot) VALUES
    ('三楼自习室A', '图书馆3楼东侧', 0, 14),
    ('四楼阅览室B', '图书馆4楼',     0, 14);

INSERT INTO t_seat (room_id, seat_no) VALUES
    (1, 'A-01'), (1, 'A-02'), (1, 'A-03'), (1, 'A-04'),
    (2, 'B-01'), (2, 'B-02'), (2, 'B-03');
