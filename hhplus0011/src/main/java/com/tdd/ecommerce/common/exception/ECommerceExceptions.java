package com.tdd.ecommerce.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ECommerceExceptions implements ErrorCode{
    INVALID_CUSTOMER(HttpStatus.BAD_REQUEST, "존재하지 않는 고객입니다."),
    INVALID_PRODUCT(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    INVALID_CART(HttpStatus.BAD_REQUEST, "장바구니가 없습니다."),
    OUT_OF_STOCK(HttpStatus.NOT_ACCEPTABLE, "재고가 없습니다."),
    INSUFFICIENCY_BALANCE(HttpStatus.NOT_ACCEPTABLE, "잔액이 부족합니다."),
    INVALID_AMOUNT(HttpStatus.NOT_ACCEPTABLE, "유효하지 않은 금액입니다."),
    FAILED_ORDER(HttpStatus.NOT_ACCEPTABLE, "주문에 실패했습니다."),
    ALREADY_ORDERED(HttpStatus.NOT_ACCEPTABLE, "이미 주문한 데이터입니다."),
    INVALID_DATE(HttpStatus.NOT_MODIFIED, "잘못된 날짜 입력");

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
