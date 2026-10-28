package com.zjc.libraryseat.dto;

import lombok.Data;

@Data
public class RoomUsageDTO {
    private Long roomId;
    private String roomName;
    private Integer occupiedSlots;
}
