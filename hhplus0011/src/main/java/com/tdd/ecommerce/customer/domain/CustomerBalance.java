package com.tdd.ecommerce.customer.domain;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;

public class CustomerBalance {
    private Long balance;

    public CustomerBalance(Long balance) {
        this.balance = balance;
    }

    public void checkSufficientBalance(Long requiredAmount) {
        if(balance < requiredAmount) {
            throw new BusinessException(ECommerceExceptions.INSUFFICIENCY_BALANCE);
        }
    }

    public void charge(Long amount) {
        if (amount < 0) {
            throw new BusinessException(ECommerceExceptions.INVALID_AMOUNT);
        }
        this.balance += amount;
    }

    public void use(Long amount) {
        checkSufficientBalance(amount);
        this.balance -= amount;
    }

    public Long getBalance() {
        return this.balance;
    }
}
