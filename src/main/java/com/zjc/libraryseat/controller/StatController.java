package com.zjc.libraryseat.controller;

import com.zjc.libraryseat.dto.RoomUsageDTO;
import com.zjc.libraryseat.dto.SlotHeatDTO;
import com.zjc.libraryseat.service.StatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/stat")
public class StatController {
private final StatService statService;
public StatController(StatService statService) {
this.statService = statService;
}
@GetMapping("room-usage")
public List<RoomUsageDTO> getRoomUsage(@RequestParam String date) {
    return statService.roomUsage(LocalDate.parse(date));
}
@GetMapping("/slot-heat")
public List<SlotHeatDTO> getSlotHeat(@RequestParam String date) {
    return statService.slotHeat(LocalDate.parse(date));
}
}
