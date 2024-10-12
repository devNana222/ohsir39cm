package com.tdd.ecommerce.order.dto;

public record OrderHistory(Long productId, Long soldAmount, String regDate) {
}
