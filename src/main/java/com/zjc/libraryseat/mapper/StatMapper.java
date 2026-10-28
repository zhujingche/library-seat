package com.zjc.libraryseat.mapper;

import com.zjc.libraryseat.dto.RoomUsageDTO;
import com.zjc.libraryseat.dto.SlotHeatDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface StatMapper {
@Select("""
SELECT r.id AS roomId,r.name AS roomname,COUNT(rs.id) AS occupiedSlots
FROM t_room r
LEFT JOIN t_seat s ON s.room_id=r.id
LEFT JOIN t_reservation_slot rs ON rs.seat_id=s.id
AND rs.reserve_date=#{date}
GROUP by r.id,r.name
ORDER BY occupiedSlots DESC
""")
List<RoomUsageDTO> roomUsage(@Param("date") LocalDate date);
@Select("""
SELECT slot_no  AS slotno ,COUNT(*) AS slotCount
FROM t_reservation_slot
WHERE reserve_date=#{date}
GROUP BY slot_no
ORDER BY slotCount DESC 
LIMIT 5 
""")
    List<SlotHeatDTO> slotHeat(@Param("date") LocalDate date);
}
