package com.automation.models;

import java.math.BigDecimal;
import java.util.Objects;

public record Product(String name, BigDecimal price) {
    public Product {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        Objects.requireNonNull(price, "Product price must not be null");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Product price must not be negative");
        }
    }
}
