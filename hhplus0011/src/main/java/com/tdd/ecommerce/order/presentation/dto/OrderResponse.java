package com.tdd.ecommerce.order.presentation.dto;

import com.tdd.ecommerce.order.infrastructure.OrderProduct;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private Long remainBalance;
    private LocalDateTime orderDate;
    private Long totalPrice;
    private Long status;
    private List<OrderProduct> orderProducts;
}
