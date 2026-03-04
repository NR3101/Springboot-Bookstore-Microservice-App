package com.neeraj.orderservice.domain.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderItem(
        @NotBlank(message = "Item code is required") String code,
        @NotBlank(message = "Item name is required") String name,
        @NotNull(message = "Item quantity is required") @Min(value = 1, message = "Item quantity must be greater than 0") Integer quantity,
        @NotBlank(message = "Item price is required") BigDecimal price) {}
