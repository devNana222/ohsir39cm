package com.tdd.ecommerce.product.dto;

public record ProductInfo (Long productId, String productName, Long price, Long stock){
    public static ProductInfo sampleProduct(){
        return new ProductInfo(1L, "sampleProduct1", 1330L, 5L);
    }

}
