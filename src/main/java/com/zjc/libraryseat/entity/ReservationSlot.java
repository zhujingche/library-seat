package com.zjc.libraryseat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
@Data
@TableName("t_reservation_slot")
public class ReservationSlot {
    private Long id;
    private Long reservationId;
    private Long seatId;
    private LocalDate reserveDate;
    private Integer slotNo;
}
