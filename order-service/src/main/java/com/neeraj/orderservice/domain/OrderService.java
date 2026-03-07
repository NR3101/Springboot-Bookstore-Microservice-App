package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.domain.models.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;
    private final OrderEventService orderEventService;

    private static final List<String> DELIVERABLE_LOCATIONS = List.of("INDIA", "USA", "CANADA", "AUSTRALIA", "UK");

    public CreateOrderResponse createOrder(String userName, @Valid CreateOrderRequest request) {
        orderValidator.validate(request);

        OrderEntity newOrder = OrderMapper.toOrderEntity(request);
        newOrder.setUserName(userName);
        OrderEntity savedOrder = orderRepository.save(newOrder);
        log.info("Saved order for user: {} with order number: {}", userName, savedOrder.getOrderNumber());

        OrderCreatedEvent event = OrderEventMapper.buildOrderCreatedEvent(savedOrder);
        orderEventService.save(event);
        return new CreateOrderResponse(savedOrder.getOrderNumber());
    }

    public void processNewOrders() {
        List<OrderEntity> newOrders = orderRepository.findByStatus(OrderStatus.NEW);

        log.info("Found {} new orders to process", newOrders.size());

        for (OrderEntity order : newOrders) {
            this.processOrder(order);
        }
    }

    private void processOrder(OrderEntity order) {
        try {
            if (canBeDelivered(order)) {
                log.info("Order {} can be delivered. Marking as DELIVERED", order.getOrderNumber());
                orderRepository.updateOrderStatus(order.getOrderNumber(), OrderStatus.DELIVERED);
                orderEventService.save(OrderEventMapper.buildOrderDeliveredEvent(order));
            } else {
                log.info("Order {} cannot be delivered yet. Will retry later.", order.getOrderNumber());
                orderRepository.updateOrderStatus(order.getOrderNumber(), OrderStatus.CANCELLED);
                orderEventService.save(
                        OrderEventMapper.buildOrderCancelledEvent(order, "Order cannot be delivered to this location"));
            }
        } catch (RuntimeException e) {
            log.error("Error processing order {}: {}", order.getOrderNumber(), e.getMessage());
            orderRepository.updateOrderStatus(order.getOrderNumber(), OrderStatus.ERROR);
            orderEventService.save(OrderEventMapper.buildOrderErrorEvent(order, e.getMessage()));
        }
    }

    private boolean canBeDelivered(OrderEntity order) {
        String location = order.getDeliveryAddress().country().toUpperCase();
        return DELIVERABLE_LOCATIONS.contains(location);
    }

    public List<OrderSummary> getOrdersForUser(String userName) {
        return orderRepository.findByUserName(userName);
    }

    public Optional<OrderDTO> getOrderForUser(String userName, String orderNumber) {
        return orderRepository
                .findByUserNameAndOrderNumber(userName, orderNumber)
                .map(OrderMapper::toOrderDTO);
    }
}
