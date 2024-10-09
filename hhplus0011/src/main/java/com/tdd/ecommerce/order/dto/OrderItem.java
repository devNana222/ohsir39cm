package com.tdd.ecommerce.order.dto;

public record OrderItem(Long productId, Long amount, Long price) {
}
