package com.neeraj.orderservice.web.controllers;

import com.neeraj.orderservice.domain.OrderService;
import com.neeraj.orderservice.domain.SecurityService;
import com.neeraj.orderservice.domain.models.CreateOrderRequest;
import com.neeraj.orderservice.domain.models.CreateOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityService securityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String userName = securityService.getLoginUserName();
        log.info("Received order request for user: {}", userName);
        return orderService.createOrder(userName, request);
    }
}
