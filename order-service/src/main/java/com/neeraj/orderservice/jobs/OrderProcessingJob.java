package com.neeraj.orderservice.jobs;

import com.neeraj.orderservice.domain.OrderService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class OrderProcessingJob {

    private final OrderService orderService;

    @Scheduled(cron = "${orders.process-new-orders-job-cron}")
    @SchedulerLock(name = "processNewOrders")
    public void processNewOrders() {
        LockAssert.assertLocked();

        log.info("Starting New Orders Processing Job at: {}", Instant.now());
        orderService.processNewOrders();
    }
}
