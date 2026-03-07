package com.neeraj.notificationservice.events;

import com.neeraj.notificationservice.domain.models.OrderCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    @RabbitListener(queues = "${notifications.new-orders-queue}")
    void handleOrderCreatedEvent(OrderCreatedEvent event) {
        System.out.println("Handling Order Created Event for event: " + event);
    }
}
