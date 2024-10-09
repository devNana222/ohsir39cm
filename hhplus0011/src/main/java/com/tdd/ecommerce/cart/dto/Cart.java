package com.tdd.ecommerce.cart.dto;

public record Cart(
            Long cartSeq
        ,   Long userCartId
        ,   Long productId
        ,   Long amount) {
}
