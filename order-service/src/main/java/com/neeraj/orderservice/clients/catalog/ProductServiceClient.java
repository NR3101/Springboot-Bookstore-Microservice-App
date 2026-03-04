package com.neeraj.orderservice.clients.catalog;

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

    public Optional<Product> getProductByCode(String code) {
        log.info("Fetching product details for code: {}", code);
        try {
            var product = restClient
                    .get()
                    .uri("/api/products/{code}", code)
                    .retrieve()
                    .body(Product.class);

            return Optional.ofNullable(product);
        } catch (Exception e) {
            log.error("Error fetching product details for code: {}", code, e);
            return Optional.empty();
        }
    }
}
