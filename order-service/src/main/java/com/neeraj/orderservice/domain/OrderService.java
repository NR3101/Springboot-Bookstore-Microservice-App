package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.domain.models.CreateOrderRequest;
import com.neeraj.orderservice.domain.models.CreateOrderResponse;
import jakarta.validation.Valid;
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

    public CreateOrderResponse createOrder(String userName, @Valid CreateOrderRequest request) {
        OrderEntity newOrder = OrderMapper.toOrderEntity(request);
        newOrder.setUserName(userName);
        OrderEntity savedOrder = orderRepository.save(newOrder);
        log.info("Saved order for user: {} with order number: {}", userName, savedOrder.getOrderNumber());
        return new CreateOrderResponse(savedOrder.getOrderNumber());
    }
}
