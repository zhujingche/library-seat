package com.zjc.libraryseat.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zjc.libraryseat.constant.ReservationStatus;
import com.zjc.libraryseat.constant.SlotTime;
import com.zjc.libraryseat.dto.ReservationRequest;
import com.zjc.libraryseat.entity.Reservation;
import com.zjc.libraryseat.entity.ReservationSlot;
import com.zjc.libraryseat.entity.Room;
import com.zjc.libraryseat.entity.Seat;
import com.zjc.libraryseat.exception.BusinessException;
import com.zjc.libraryseat.mapper.ReservationMapper;
import com.zjc.libraryseat.mapper.ReservationSlotMapper;
import com.zjc.libraryseat.mapper.RoomMapper;
import com.zjc.libraryseat.mapper.SeatMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        if (req.getSeatId() == null || req.getDate() == null
                || req.getStartSlot() == null || req.getEndSlot() == null||req.getUserId()==null) {
            throw new BusinessException("参数不完整,再检查一下吧~");
        }
        if (req.getDate().isBefore(LocalDate.now())) {
            throw new BusinessException("不能预约过去的日期哦~");
        }
        Seat seat = seatMapper.selectById(req.getSeatId());
        if(seat == null){
            throw new BusinessException("该座位不存在");
        }
        if(seat.getStatus()==0)
        {
            throw new BusinessException("该座位正在维修,暂不可用");
        }
        Room room = roomMapper.selectById(seat.getRoomId());
        if (room == null) {
            throw new BusinessException("该座位所属的阅览室不存在");
        }
        boolean valid = req.getStartSlot() >= room.getOpenSlot()
                && req.getEndSlot() <= room.getCloseSlot()
                && req.getStartSlot() < req.getEndSlot();
        if (!valid)
        {
            throw new BusinessException("所填时间范围有误哦~");
        }
        QueryWrapper<ReservationSlot> wrapper = new QueryWrapper<>();
        wrapper.eq("seat_id", seat.getId());
        wrapper.eq("reserve_date", req.getDate());
        List<ReservationSlot> occupied = reservationSlotMapper.selectList(wrapper);
        for(ReservationSlot slot : occupied){
            if(slot.getSlotNo()>=req.getStartSlot()&&slot.getSlotNo()<req.getEndSlot()){
                throw new BusinessException("该时段已经被占用了哦~");
            }
        }
        Reservation r= new Reservation();
        r.setSeatId(seat.getId());
        r.setReserveDate(req.getDate());
        r.setStartSlot(req.getStartSlot());
        r.setEndSlot(req.getEndSlot());
        r.setUserId(req.getUserId());
        r.setStatus(ReservationStatus.RESERVED);
        reservationMapper.insert(r);
        try {
            for(int i=req.getStartSlot();i<req.getEndSlot();i++){
                ReservationSlot slot = new ReservationSlot();
                slot.setReservationId(r.getId());
                slot.setSlotNo(i);
                slot.setReserveDate(req.getDate());
                slot.setSeatId(seat.getId());
                reservationSlotMapper.insert(slot);
            }
        }catch(DuplicateKeyException e){
            throw new BusinessException("该时段已经被预约了哦,请换一个吧~");
        }
        return r.getId();
    }
    @Transactional
    public void cancelReservation(Long id)
    {
        Reservation r = reservationMapper.selectById(id);
        if(r==null)
        {
            throw new BusinessException("预约不存在哦,检查一下吧~");
        }
        if(!ReservationStatus.canTransit(r.getStatus(),ReservationStatus.CANCELLED))
        {
            throw new BusinessException("只有已预约状态的才可取消预约,当前状态为"+r.getStatus());
        }
        QueryWrapper<ReservationSlot> wrapper = new QueryWrapper<>();
        wrapper.eq("reservation_id",id);
        reservationSlotMapper.delete(wrapper);
        r.setStatus(ReservationStatus.CANCELLED);
        reservationMapper.updateById(r);
    }
    public void checkin(Long id)
    {
        Reservation r = reservationMapper.selectById(id);
        if(r==null)
        {
            throw new BusinessException("该预约不存在哦~");
        }
        if(!ReservationStatus.canTransit(r.getStatus(),ReservationStatus.CHECKED_IN))
        {
            throw new BusinessException("当前状态["+r.getStatus()+"]不允许签到!");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime starttime= SlotTime.totime(r.getReserveDate(),r.getStartSlot());
        LocalDateTime endtime= SlotTime.totime(r.getReserveDate(),r.getEndSlot());
        if(now.isBefore(starttime.minusMinutes(15)))
        {
            throw new BusinessException("还没有到你预约的时段哦~(可提前十五分钟签到)");
        }
        if(!now.isBefore(endtime))
        {
throw new BusinessException("你的预约时段已经过了哦~");
        }
        r.setStatus(ReservationStatus.CHECKED_IN);
        reservationMapper.updateById(r);
    }
@Transactional
    public int releaseExpiredReservations()
{
    QueryWrapper<Reservation> wrapper = new QueryWrapper<>();
    wrapper.eq("status",ReservationStatus.RESERVED);
    List<Reservation> reservations = reservationMapper.selectList(wrapper);
    int count=0;
    LocalDateTime now = LocalDateTime.now();
    for(Reservation r : reservations)
    {
        LocalDateTime endtime= SlotTime.totime(r.getReserveDate(),r.getEndSlot());
        if(now.isBefore(endtime))
        {
            continue;
        }
        UpdateWrapper<Reservation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",r.getId());
        updateWrapper.eq("status",ReservationStatus.RESERVED);
        updateWrapper.set("status",ReservationStatus.NO_SHOW);
        int rows = reservationMapper.update(null, updateWrapper);
        if(rows==0)
        {
            continue;
        }
        QueryWrapper<ReservationSlot> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("reservation_id",r.getId());
      reservationSlotMapper.delete(wrapper1);
      count++;
    }
    return count;
}

}