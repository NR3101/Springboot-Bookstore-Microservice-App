package com.neeraj.notificationservice.domain.models;

import java.time.LocalDateTime;
import java.util.Set;

public record OrderCancelledEvent(
        String eventId, // Used for idempotency and tracing
        String orderNumber,
        Set<OrderItem> items,
        Customer customer,
        Address deliveryAddress,
        String reason,
        LocalDateTime createdAt) {}
