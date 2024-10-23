package com.tdd.ecommerce.customer.presentation.dto;


public record ChargeRequest(Long balance) {

    public void validate() {
        if (this.balance <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야합니다.");
        }
        if (this.balance % 10 != 0) {
            throw new IllegalArgumentException("1원 단위는 충전할 수 없습니다.");
        }
        if (this.balance < 500L || this.balance > 100000L) {
            throw new IllegalArgumentException("1회 충전 금액은 500원 이상 100,000원 이하여야 합니다.");
        }
    }
}
