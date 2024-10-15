package com.tdd.ecommerce.common.model;

import lombok.Data;

@Data
public class CommonApiResponse<T> {
    private boolean result;
    private String message;
    private T data;

    public CommonApiResponse(boolean result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

}
