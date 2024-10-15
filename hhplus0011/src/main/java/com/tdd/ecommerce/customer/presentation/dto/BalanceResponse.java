package com.tdd.ecommerce.customer.presentation.dto;

import lombok.Data;

@Data
public class BalanceResponse {
    private Long customerId;
    private Long balance;

    public BalanceResponse(Long customerId, Long balance) {
        this.customerId = customerId;
        this.balance = balance;
    }
}
