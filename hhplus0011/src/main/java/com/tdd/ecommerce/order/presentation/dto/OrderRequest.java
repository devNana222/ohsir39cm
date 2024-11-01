package com.tdd.ecommerce.order.presentation.dto;

import com.tdd.ecommerce.order.domain.OrderProduct;
import lombok.Getter;

import java.util.List;

@Getter

public class OrderRequest {
    private Long customerId;
    private List<OrderProduct> orderProducts;
}
