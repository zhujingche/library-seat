package com.zjc.libraryseat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_seat")
public class Seat {
private Long id;
private String roomId;
private String seatNo;
private String status;

}
