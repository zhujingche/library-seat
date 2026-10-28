package com.zjc.libraryseat.dto;

import lombok.Data;
@Data
public class SlotHeatDTO {
        private Integer slotNo;         // 第几格
        private Integer slotCount;      // 被占用了多少次
    }
