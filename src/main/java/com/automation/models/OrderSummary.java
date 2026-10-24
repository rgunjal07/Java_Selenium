package com.automation.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record OrderSummary(List<Product> products, BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
    public OrderSummary {
        products = List.copyOf(products);
        Objects.requireNonNull(subtotal, "Subtotal must not be null");
        Objects.requireNonNull(tax, "Tax must not be null");
        Objects.requireNonNull(total, "Total must not be null");
        if (products.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one product");
        }
        BigDecimal itemSum = products.stream()
                .map(Product::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (itemSum.compareTo(subtotal) != 0 || subtotal.add(tax).compareTo(total) != 0) {
            throw new IllegalArgumentException("Order amounts do not match the product prices and tax");
        }
    }
}
