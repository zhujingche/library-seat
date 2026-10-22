package com.zjc.libraryseat.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjc.libraryseat.entity.ReservationSlot;
import com.zjc.libraryseat.entity.Room;
import com.zjc.libraryseat.entity.Seat;
import com.zjc.libraryseat.exception.BusinessException;
import com.zjc.libraryseat.mapper.ReservationSlotMapper;
import com.zjc.libraryseat.mapper.RoomMapper;
import com.zjc.libraryseat.mapper.SeatMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class SeatService {
    private final SeatMapper seatMapper;
    private final RoomMapper roomMapper;
    private final ReservationSlotMapper reservationSlotMapper;

    public SeatService(SeatMapper seatMapper, RoomMapper roomMapper, ReservationSlotMapper reservationSlotMapper) {
        this.seatMapper = seatMapper;
        this.roomMapper = roomMapper;
        this.reservationSlotMapper = reservationSlotMapper;
    }

    public List<Seat> listByRoom(Long roomId) {
        QueryWrapper<Seat> wrapper = new QueryWrapper<>();
        if (roomId != null) {
            wrapper.eq("room_id", roomId);
        }
        return seatMapper.selectList(wrapper);
    }

    public Seat getById(Long id) {
        return seatMapper.selectById(id);
    }

    public List<Integer> availableSlots(Long seatId, LocalDate date) {
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null) {
            throw new BusinessException("错误!没有这个座位哦~");
        }
        Room room = roomMapper.selectById(seat.getRoomId());
        if (room == null) {
            throw new BusinessException("错误!没有这个房间哦~");
        }
        Integer openSlot = room.getOpenSlot();
        Integer closeSlot = room.getCloseSlot();
        QueryWrapper<ReservationSlot> wrapper = new QueryWrapper<>();
        wrapper.eq("seat_id", seatId);
        wrapper.eq("reserve_date", date);
        List<ReservationSlot> occupied = reservationSlotMapper.selectList(wrapper);
        List<Integer> occupiedSlots = new ArrayList<>();
        for (ReservationSlot rs : occupied) {
            occupiedSlots.add(rs.getSlotNo());
        }
        List<Integer> availableSlots = new ArrayList<>();
        for (int i = openSlot; i < closeSlot; i++) {
            if (!occupiedSlots.contains(i)) {
                availableSlots.add(i);
            }
        }
        return availableSlots;
    }
}
