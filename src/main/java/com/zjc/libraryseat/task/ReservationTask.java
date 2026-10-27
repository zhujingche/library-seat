package com.zjc.libraryseat.task;
import com.zjc.libraryseat.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class ReservationTask {
    private final ReservationService reservationService;
    public ReservationTask(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @Scheduled(fixedRate =10_000)
    public void releaseExpired() {
        int count=reservationService.releaseExpiredReservations();
        if(count>0) {
            log.info("定时任务:释放了{}条超时未签到的预约",count);
        }
    }
}
