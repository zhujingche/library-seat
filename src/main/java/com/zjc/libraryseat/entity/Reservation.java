package com.zjc.libraryseat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
@Data
@TableName("t_reservation")
public class Reservation {
    private Long id;
    private Long userId;
    private Long seatId;
    private LocalDate reserveDate;
    private Integer startSlot;
    private Integer endSlot;
    private String status;
}
