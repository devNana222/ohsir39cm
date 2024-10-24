package com.tdd.ecommerce.order.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderServiceResponse {
    private Long orderId;
    private Long customerId;
    private Long balance;
    private List<OrderProductInfo> orderProducts;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProductInfo {
        private Long productId;
        private String productName;
        private Long amount;
        private Long price;
    }
}
