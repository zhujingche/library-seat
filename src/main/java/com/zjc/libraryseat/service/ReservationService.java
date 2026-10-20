package com.zjc.libraryseat.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjc.libraryseat.dto.ReservationRequest;
import com.zjc.libraryseat.entity.Reservation;
import com.zjc.libraryseat.entity.ReservationSlot;
import com.zjc.libraryseat.entity.Room;
import com.zjc.libraryseat.entity.Seat;
import com.zjc.libraryseat.mapper.ReservationMapper;
import com.zjc.libraryseat.mapper.ReservationSlotMapper;
import com.zjc.libraryseat.mapper.RoomMapper;
import com.zjc.libraryseat.mapper.SeatMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationMapper reservationMapper;
    private final RoomMapper roomMapper;
    private final SeatMapper seatMapper;
    private final ReservationSlotMapper reservationSlotMapper;

    public ReservationService(ReservationMapper reservationMapper, RoomMapper roomMapper, SeatMapper seatMapper, ReservationSlotMapper reservationSlotMapper) {
        this.reservationMapper = reservationMapper;
        this.roomMapper = roomMapper;
        this.seatMapper = seatMapper;
        this.reservationSlotMapper = reservationSlotMapper;
    }

    @Transactional
    public Long makeReservation(ReservationRequest req) {
        if (req.getSeatId() == null) {
            throw new RuntimeException("该座位不存在哦~");
        }
        Seat seat = seatMapper.selectById(req.getSeatId());
        Room room = roomMapper.selectById(seat.getRoomId());
        Integer openslot = room.getOpenSlot();
        Integer closeslot = room.getCloseSlot();
        if (openslot >= req.getStartSlot() || closeslot <= req.getEndSlot() || req.getStartSlot() < req.getEndSlot()) {
            throw new RuntimeException("所填时间范围有误哦~");
        }
        QueryWrapper<ReservationSlot> wrapper = new QueryWrapper<>();
        wrapper.eq("seat_id", seat.getId());
        wrapper.eq("reserve_date", req.getDate());
        List<ReservationSlot> occupied = reservationSlotMapper.selectList(wrapper);
        List<Integer> occupiedtime = new ArrayList<>();
        for (ReservationSlot slot : occupied) {
            occupiedtime.add(slot.getSlotNo());
        }
        for (Integer slot : occupiedtime) {
            if (slot <= closeslot && slot >= openslot) {
                throw new RuntimeException("该时段已被占用~");
            }
        }
        Reservation r= new Reservation();
        r.setSeatId(seat.getId());
        r.setReserveDate(req.getDate());
        r.setStartSlot(openslot);
        r.setEndSlot(closeslot);
        r.setStatus("Reserved");
        reservationMapper.insert(r);
        try {
            for(int i=req.getStartSlot();i<=req.getEndSlot();i++){
                ReservationSlot slot = new ReservationSlot();
                slot.setReservationId(r.getId());
                slot.setSlotNo(i);
                slot.setReserveDate(req.getDate());
                slot.setSeatId(seat.getId());
                reservationSlotMapper.insert(slot);
            }
        }catch(DuplicateKeyException e){
            throw new RuntimeException("该时段以及被预约了哦,请换一个吧~");
        }
        return r.getId();
    }
}