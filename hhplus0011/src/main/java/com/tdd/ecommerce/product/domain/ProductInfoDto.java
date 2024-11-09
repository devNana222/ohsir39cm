package com.tdd.ecommerce.product.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoDto{
    private Long productId;
    private String productName;
    private Long price;
    private Long amount;
    private String category;

    public ProductInfoDto(Long productId, String productName, Long price, Long amount){
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.amount = amount;
    }

    public ProductInfoDto(Long productId, String productName, Long price, String category){
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.category = category;
    }
}
