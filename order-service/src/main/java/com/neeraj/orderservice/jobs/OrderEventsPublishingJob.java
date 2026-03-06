package com.neeraj.orderservice.jobs;

import com.neeraj.orderservice.domain.OrderEventService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class OrderEventsPublishingJob {

    private final OrderEventService orderEventService;

    @Scheduled(cron = "${orders.publish-order-events-job-cron}")
    public void publishOrderEvents() {
        log.info("Starting Order Events Publishing Job at: {}", Instant.now());
        orderEventService.publishOrderEvents();
    }
}
