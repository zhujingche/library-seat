package com.zjc.libraryseat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@Data
@TableName("t_room")
public class Room {
    private Long id;
private String name;
private String location;
private Integer openSlot;
private Integer closeSlot;
private Integer status;
}
