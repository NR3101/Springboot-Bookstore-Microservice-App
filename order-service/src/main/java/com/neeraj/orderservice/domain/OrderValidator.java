package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.clients.catalog.Product;
import com.neeraj.orderservice.clients.catalog.ProductServiceClient;
import com.neeraj.orderservice.domain.models.CreateOrderRequest;
import com.neeraj.orderservice.domain.models.OrderItem;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class OrderValidator {

    private final ProductServiceClient productServiceClient;

    void validate(CreateOrderRequest request) {
        Set<OrderItem> items = request.items();

        for (OrderItem item : items) {
            Product product = productServiceClient
                    .getProductByCode(item.code())
                    .orElseThrow(() -> new InvalidOrderException("Product with code " + item.code() + " not found"));

            if (item.price().compareTo(product.price()) != 0) {
                log.error(
                        "Price mismatch for product code: {}. Expected: {}, Provided: {}",
                        item.code(),
                        product.price(),
                        item.price());
                throw new InvalidOrderException("Price mismatch for product code: " + item.code());
            }
        }
    }
}
