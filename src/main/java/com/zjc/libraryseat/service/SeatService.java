package com.zjc.libraryseat.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjc.libraryseat.entity.Seat;
import com.zjc.libraryseat.mapper.SeatMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SeatService {
    private final SeatMapper seatMapper;
    public SeatService(SeatMapper seatMapper)
    {
        this.seatMapper=seatMapper;
    }
    public List<Seat> listByRoom(Long roomId)
    {
        QueryWrapper<Seat> wrapper=new QueryWrapper<>();
        if(roomId!=null)
        {
            wrapper.eq("room_id",roomId);
        }
        return seatMapper.selectList(wrapper);
    }
    public Seat getById(Long id)
    {
        return seatMapper.selectById(id);
    }

}
