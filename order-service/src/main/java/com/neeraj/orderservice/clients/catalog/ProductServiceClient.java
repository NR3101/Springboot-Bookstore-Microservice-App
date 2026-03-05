package com.neeraj.orderservice.clients.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Log4j2
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestClient restClient;

    // Method to fetch product details by code with retry, circuit breaker, and fallback
    @CircuitBreaker(name = "catalog-service")
    @Retry(name = "catalog-service", fallbackMethod = "getProductByCodeFallback")
    public Optional<Product> getProductByCode(String code) {
        log.info("Fetching product details for code: {}", code);

        var product =
                restClient.get().uri("/api/products/{code}", code).retrieve().body(Product.class);

        return Optional.ofNullable(product);
    }

    /*
     * NOTE: 1. The signature of the fallback method must match the original method's parameters plus an additional Throwable parameter to capture the exception that caused the fallback. The return type should also match the original method's return type.
     * 2. In case we use both @Retry and @CircuitBreaker annotations,it is best to use fallback for only @Retry, as the fallback will be triggered for each retry attempt, allowing you to handle transient failures more effectively. If you use a fallback for @CircuitBreaker, it will only be triggered when the circuit breaker is open, which may not provide the same level of resilience for transient issues.
     * 3. The order of resilience annotations is usually @Retry followed by @CircuitBreaker.
     */

    // Fallback method to return an empty Optional when the product details cannot be fetched.
    public Optional<Product> getProductByCodeFallback(String code, Throwable ex) {
        log.error("Failed to fetch product details for code: {}. Error: {}", code, ex.getMessage());
        return Optional.empty();
    }
}
