package com.neeraj.orderservice.web.controllers;

import com.neeraj.orderservice.domain.OrderNotFoundException;
import com.neeraj.orderservice.domain.OrderService;
import com.neeraj.orderservice.domain.SecurityService;
import com.neeraj.orderservice.domain.models.CreateOrderRequest;
import com.neeraj.orderservice.domain.models.CreateOrderResponse;
import com.neeraj.orderservice.domain.models.OrderDTO;
import com.neeraj.orderservice.domain.models.OrderSummary;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "security_auth")
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

    @GetMapping
    List<OrderSummary> getOrders() {
        String userName = securityService.getLoginUserName();
        log.info("Received request to fetch orders for user: {}", userName);
        return orderService.getOrdersForUser(userName);
    }

    @GetMapping("/{orderNumber}")
    OrderDTO getOrder(@PathVariable String orderNumber) {
        String userName = securityService.getLoginUserName();
        log.info("Received request to fetch order {} for user: {}", orderNumber, userName);
        return orderService
                .getOrderForUser(userName, orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderNumber));
    }
}
