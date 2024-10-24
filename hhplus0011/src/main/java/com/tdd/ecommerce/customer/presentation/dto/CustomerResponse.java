package com.tdd.ecommerce.customer.presentation.dto;

import lombok.Data;

@Data
public class CustomerResponse {
    private Long customerId;
    private Long balance;

    public CustomerResponse(Long customerId, Long balance) {
        this.customerId = customerId;
        this.balance = balance;
    }
}
