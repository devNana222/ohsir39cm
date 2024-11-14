package com.tdd.ecommerce.order.presentation.dto;
import lombok.Getter;

import java.util.List;

@Getter

public class OrderRequest {
    private Long customerId;
    private List<OrderProductRequest> orderProducts;
}
