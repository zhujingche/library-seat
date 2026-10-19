package com.zjc.libraryseat.controller;
import com.zjc.libraryseat.entity.Seat;
import com.zjc.libraryseat.service.SeatService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/seat")
public class SeatController {
    private final SeatService seatService;
    public SeatController(SeatService seatService)
    {
        this.seatService=seatService;
    }
    @GetMapping("/list")
    public List<Seat> listByRoom(@RequestParam(required = false) Long roomId)
    {
        return seatService.listByRoom(roomId);
    }
    @GetMapping("/{id}")
    public Seat getSeatById(@PathVariable Long id)
    {
        return seatService.getById(id);
    }

}
