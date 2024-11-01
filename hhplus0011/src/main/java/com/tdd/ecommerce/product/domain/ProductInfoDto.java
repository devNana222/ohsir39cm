package com.tdd.ecommerce.product.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductInfoDto{
    private Long productId;
    private String productName;
    private Long price;
    private Long amount;

    public ProductInfoDto(Long productId, String productName, Long price){
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }
}
