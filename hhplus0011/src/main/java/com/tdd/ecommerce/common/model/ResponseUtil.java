package com.tdd.ecommerce.common.model;

import com.tdd.ecommerce.common.exception.ErrorCode;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<CommonApiResponse<?>> buildErrorResponse(ErrorCode errorCode, String errorMessage) {
        CommonApiResponse<ErrorCode> errorResponse = new CommonApiResponse<>(false, errorMessage, errorCode);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    public static ResponseEntity<CommonApiResponse<?>> buildSuccessResponse(String message, Object data) {
        CommonApiResponse<Object> response = new CommonApiResponse<>(true, message, data);
        return ResponseEntity.ok(response);
    }
}
