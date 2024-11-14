package com.tdd.ecommerce.order.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductRequest {
    private Long productId;
    private Long amount;
}
