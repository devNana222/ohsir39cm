package com.tdd.ecommerce.product.application;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductInfo {
    private Long productId;
    private String productName;
    private Long price;
    private Long amount;

    public ProductInfo(Long productId, String productName, Long price){
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }
}
