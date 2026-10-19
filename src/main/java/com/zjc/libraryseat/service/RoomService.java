package com.zjc.libraryseat.service;

import com.zjc.libraryseat.entity.Room;
import com.zjc.libraryseat.mapper.RoomMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomMapper roomMapper;
    public RoomService(RoomMapper roomMapper)
    {
        this.roomMapper=roomMapper;
    }
public List<Room> listAll()
{
    return roomMapper.selectList(null);
}
public Room getById(Long id)
{
return roomMapper.selectById(id);
}
}
