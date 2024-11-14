package com.tdd.ecommerce.order.application;

import lombok.Data;

@Data
public class OrderInfo {
    private Long productId;
    private Long amount;
}
