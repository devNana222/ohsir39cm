package com.tdd.ecommerce.cart.application.dto;

import com.tdd.ecommerce.product.domain.ProductInfoDto;
import com.tdd.ecommerce.product.infrastructure.Product;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartDetailResponse {
    ProductInfoDto product;
    Long productAmount;
}

