package com.zjc.libraryseat.controller;
import com.zjc.libraryseat.dto.ReservationRequest;
import com.zjc.libraryseat.service.ReservationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
private final ReservationService reservationService;
public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
}
@PostMapping
    public Long addReservation(@RequestBody ReservationRequest req){
    return reservationService.makeReservation(req);}
   @DeleteMapping("/{id}")
           public void deleteReservation(@PathVariable Long id)
    {
        reservationService.cancelReservation(id);
    }
    @PostMapping("/{id}/checkin")
    public void checkin(@PathVariable Long id){
    reservationService.checkin(id);
    }
}

