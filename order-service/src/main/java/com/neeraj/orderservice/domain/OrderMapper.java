package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.domain.models.CreateOrderRequest;
import com.neeraj.orderservice.domain.models.OrderItem;
import com.neeraj.orderservice.domain.models.OrderStatus;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderEntity toOrderEntity(CreateOrderRequest request) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNumber(UUID.randomUUID().toString());
        orderEntity.setStatus(OrderStatus.NEW);
        orderEntity.setCustomer(request.customer());
        orderEntity.setDeliveryAddress(request.deliveryAddress());

        Set<OrderItemEntity> orderItems = request.items().stream()
                .map(item -> {
                    OrderItemEntity orderItem = toOrderItemEntity(item);
                    orderItem.setOrder(orderEntity);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        orderEntity.setOrderItems(orderItems);
        return orderEntity;
    }

    private static OrderItemEntity toOrderItemEntity(OrderItem item) {
        return OrderItemEntity.builder()
                .code(item.code())
                .name(item.name())
                .quantity(item.quantity())
                .price(item.price())
                .build();
    }
}
