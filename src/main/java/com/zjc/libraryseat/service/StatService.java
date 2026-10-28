package com.zjc.libraryseat.service;

import com.zjc.libraryseat.dto.RoomUsageDTO;
import com.zjc.libraryseat.dto.SlotHeatDTO;
import com.zjc.libraryseat.mapper.StatMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class StatService {
    private final StatMapper statMapper;
    public StatService(StatMapper statMapper) {
        this.statMapper = statMapper;
    }
    public List<RoomUsageDTO> roomUsage(LocalDate date) {
        return statMapper.roomUsage(date);
    }
    public List<SlotHeatDTO>  slotHeat(LocalDate date) {
        return statMapper.slotHeat(date);
    }
}
