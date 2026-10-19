package com.zjc.libraryseat.controller;

import com.zjc.libraryseat.entity.Room;
import com.zjc.libraryseat.service.RoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;
    public RoomController(RoomService roomService)
    {
        this.roomService=roomService;
    }
    @GetMapping("/list")
    public List<Room> listAll()
    {
        return roomService.listAll();
    }
    @GetMapping("/{id}")
    public Room getById(@PathVariable Long id)
    {
        return roomService.getById(id);
    }


}
