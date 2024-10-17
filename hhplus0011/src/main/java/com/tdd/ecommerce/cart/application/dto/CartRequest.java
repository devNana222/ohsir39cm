package com.tdd.ecommerce.cart.application;

public record CartRequest(
        Long productId,
        Long amount
) {
}
