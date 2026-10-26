package com.zjc.libraryseat.constant;

import java.time.LocalDate;
import java.time.LocalDateTime;

public  final class SlotTime {
    public static final int Base_Hour = 8;
    public static LocalDateTime totime(LocalDate date,int slot)
    {
        return date.atTime(Base_Hour,0).plusHours(slot);
    }
}
