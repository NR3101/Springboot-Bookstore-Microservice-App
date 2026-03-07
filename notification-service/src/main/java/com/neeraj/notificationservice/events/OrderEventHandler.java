package com.neeraj.notificationservice.events;

import com.neeraj.notificationservice.domain.NotificationService;
import com.neeraj.notificationservice.domain.OrderEventEntity;
import com.neeraj.notificationservice.domain.OrderEventRepository;
import com.neeraj.notificationservice.domain.models.OrderCancelledEvent;
import com.neeraj.notificationservice.domain.models.OrderCreatedEvent;
import com.neeraj.notificationservice.domain.models.OrderDeliveredEvent;
import com.neeraj.notificationservice.domain.models.OrderErrorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class OrderEventHandler {

    private final NotificationService notificationService;
    private final OrderEventRepository orderEventRepository;

    @RabbitListener(queues = "${notifications.new-orders-queue}")
    void handleOrderCreatedEvent(OrderCreatedEvent event) {
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Duplicate event received with eventId: {}, ignoring...", event.eventId());
            return;
        }

        notificationService.sendOrderCreatedNotification(event);
        OrderEventEntity eventEntity =
                OrderEventEntity.builder().eventId(event.eventId()).build();
        orderEventRepository.save(eventEntity);
    }

    @RabbitListener(queues = "${notifications.delivered-orders-queue}")
    void handleOrderDeliveredEvent(OrderDeliveredEvent event) {
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Duplicate event received with eventId: {}, ignoring...", event.eventId());
            return;
        }

        notificationService.sendOrderDeliveredNotification(event);
        OrderEventEntity eventEntity =
                OrderEventEntity.builder().eventId(event.eventId()).build();
        orderEventRepository.save(eventEntity);
    }

    @RabbitListener(queues = "${notifications.cancelled-orders-queue}")
    void handleOrderCancelledEvent(OrderCancelledEvent event) {
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Duplicate event received with eventId: {}, ignoring...", event.eventId());
            return;
        }

        notificationService.sendOrderCancelledNotification(event);
        OrderEventEntity eventEntity =
                OrderEventEntity.builder().eventId(event.eventId()).build();
        orderEventRepository.save(eventEntity);
    }

    @RabbitListener(queues = "${notifications.error-orders-queue}")
    void handleOrderErrorEvent(OrderErrorEvent event) {
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Duplicate event received with eventId: {}, ignoring...", event.eventId());
            return;
        }

        notificationService.sendOrderErrorNotification(event);
        OrderEventEntity eventEntity =
                OrderEventEntity.builder().eventId(event.eventId()).build();
        orderEventRepository.save(eventEntity);
    }
}
