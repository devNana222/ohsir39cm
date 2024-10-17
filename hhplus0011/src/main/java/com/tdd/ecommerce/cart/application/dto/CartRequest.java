package com.tdd.ecommerce.cart.application.dto;

import jakarta.validation.ValidationException;

public record CartRequest(
        Long productId,
        Long amount
) {

    public void validate(){
        if(this.amount == 0){
            throw new ValidationException("변경수량은 0일 수 없습니다.");
        }
    }
}
