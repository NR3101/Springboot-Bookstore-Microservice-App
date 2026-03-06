package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.domain.models.OrderCreatedEvent;
import com.neeraj.orderservice.domain.models.OrderItem;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderEventMapper {
    static OrderCreatedEvent buildOrderCreatedEvent(OrderEntity orderEntity) {
        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                orderEntity.getOrderNumber(),
                getOrderItems(orderEntity),
                orderEntity.getCustomer(),
                orderEntity.getDeliveryAddress(),
                LocalDateTime.now());
    }

    public static Set<OrderItem> getOrderItems(OrderEntity orderEntity) {
        return orderEntity.getOrderItems().stream()
                .map(item -> new OrderItem(item.getCode(), item.getName(), item.getQuantity(), item.getPrice()))
                .collect(Collectors.toSet());
    }
}
