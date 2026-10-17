package com.zjc.libraryseat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@Data
@TableName("t_room")
public class Room {
    private Long id;
private String name;
private String location;
private Integer open_slot;
private Integer close_slot;
private Integer status;
}
