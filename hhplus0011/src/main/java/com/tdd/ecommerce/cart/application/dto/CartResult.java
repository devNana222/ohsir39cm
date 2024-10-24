package com.tdd.ecommerce.cart.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CartResult {
        private Long customerId;
        private List<CartDetailResponse> productInfoDtoList;
        private LocalDateTime regDate;
}