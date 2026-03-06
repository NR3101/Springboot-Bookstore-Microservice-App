package com.neeraj.orderservice.jobs;

import com.neeraj.orderservice.domain.OrderService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class OrderProcessingJob {

    private final OrderService orderService;

    @Scheduled(cron = "${orders.process-new-orders-job-cron}")
    public void processNewOrders() {
        log.info("Starting New Orders Processing Job at: {}", Instant.now());
        orderService.processNewOrders();
    }
}
