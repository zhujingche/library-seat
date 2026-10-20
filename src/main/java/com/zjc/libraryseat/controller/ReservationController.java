package com.zjc.libraryseat.controller;
import com.zjc.libraryseat.dto.ReservationRequest;
import com.zjc.libraryseat.service.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/reservation")
public class ReservationController {
private final ReservationService reservationService;
public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
}
@PostMapping
    public Long addReservation(@RequestBody ReservationRequest req){
    return reservationService.makeReservation(req);
}
}
