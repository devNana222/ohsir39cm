package com.tdd.ecommerce.product.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class ProductServiceResponse {
    private Long productId;
    private String productName;
    private String category;
    private Long price;
    private Long amount;

    @JsonCreator
    public ProductServiceResponse(
            @JsonProperty("productId") Long productId,
            @JsonProperty("productName") String productName,
            @JsonProperty("category") String category,
            @JsonProperty("price") Long price,
            @JsonProperty("amount")Long amount) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.amount = amount;
        this.category = category;
    }
}
