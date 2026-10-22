package com.zjc.libraryseat.dto;

import lombok.Data;

import java.time.LocalDate;

@Data

public class ReservationRequest {
    private Long userId;
    private Long seatId;
    private LocalDate date;
    private Integer startSlot;
    private Integer endSlot;

}
