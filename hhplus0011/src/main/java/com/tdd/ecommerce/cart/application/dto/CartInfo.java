package com.tdd.ecommerce.cart.application.dto;

public record CartInfo(
        Long productId,
        Long amount
) {
}
