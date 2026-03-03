package com.neeraj.catalogservice;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "catalog")
public record ApplicationProperties(
        @DefaultValue("10") @Min(value = 1, message = "Page size must be greater than 0") int pageSize) {}
