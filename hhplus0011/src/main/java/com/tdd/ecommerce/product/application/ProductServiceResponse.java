package com.tdd.ecommerce.product.application;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductServiceResponse {
    private Long productId;
    private String productName;
    private String category;
    private Long price;
    private Long amount;
}
