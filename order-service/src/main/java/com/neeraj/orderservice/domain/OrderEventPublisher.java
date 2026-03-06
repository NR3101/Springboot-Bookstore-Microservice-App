package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.ApplicationProperties;
import com.neeraj.orderservice.domain.models.OrderCancelledEvent;
import com.neeraj.orderservice.domain.models.OrderCreatedEvent;
import com.neeraj.orderservice.domain.models.OrderDeliveredEvent;
import com.neeraj.orderservice.domain.models.OrderErrorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ApplicationProperties properties;

    public void publish(OrderCreatedEvent event) {
        this.send(properties.newOrdersQueue(), event);
    }

    public void publish(OrderDeliveredEvent event) {
        this.send(properties.deliveredOrdersQueue(), event);
    }

    public void publish(OrderCancelledEvent event) {
        this.send(properties.cancelledOrdersQueue(), event);
    }

    public void publish(OrderErrorEvent event) {
        this.send(properties.errorOrdersQueue(), event);
    }

    private void send(String routingKey, Object payload) {
        rabbitTemplate.convertAndSend(properties.orderEventsExchange(), routingKey, payload);
    }
}
