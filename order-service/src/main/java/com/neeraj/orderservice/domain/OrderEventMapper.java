package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.domain.models.*;
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

    static OrderDeliveredEvent buildOrderDeliveredEvent(OrderEntity orderEntity) {
        return new OrderDeliveredEvent(
                UUID.randomUUID().toString(),
                orderEntity.getOrderNumber(),
                getOrderItems(orderEntity),
                orderEntity.getCustomer(),
                orderEntity.getDeliveryAddress(),
                LocalDateTime.now());
    }

    static OrderCancelledEvent buildOrderCancelledEvent(OrderEntity orderEntity, String reason) {
        return new OrderCancelledEvent(
                UUID.randomUUID().toString(),
                orderEntity.getOrderNumber(),
                getOrderItems(orderEntity),
                orderEntity.getCustomer(),
                orderEntity.getDeliveryAddress(),
                reason,
                LocalDateTime.now());
    }

    static OrderErrorEvent buildOrderErrorEvent(OrderEntity orderEntity, String reason) {
        return new OrderErrorEvent(
                UUID.randomUUID().toString(),
                orderEntity.getOrderNumber(),
                getOrderItems(orderEntity),
                orderEntity.getCustomer(),
                orderEntity.getDeliveryAddress(),
                reason,
                LocalDateTime.now());
    }

    public static Set<OrderItem> getOrderItems(OrderEntity orderEntity) {
        return orderEntity.getOrderItems().stream()
                .map(item -> new OrderItem(item.getCode(), item.getName(), item.getQuantity(), item.getPrice()))
                .collect(Collectors.toSet());
    }
}
