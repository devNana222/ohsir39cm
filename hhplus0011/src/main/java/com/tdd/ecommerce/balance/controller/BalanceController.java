package com.tdd.ecommerce.balance.controller;


import com.tdd.ecommerce.balance.dto.ChargeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/customers")
@RestController
public class BalanceController {

    private static final Map<Long, Long> userBalance = new HashMap<>();

    static {
        userBalance.put(1L, 500L);
        userBalance.put(2L, 1000L);
        userBalance.put(3L, 750L);
    }


    @GetMapping("/{customer_id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable("customer_id") Long customerId) {
        Long balance = userBalance.getOrDefault(customerId, 0L);

        Map<String, Long> result = new HashMap<>();
        result.put("customer_id", customerId);
        result.put("balance", balance);

        Map<String, Object> data = new HashMap<>();
        data.put("result", true);
        data.put("message", "조회성공");
        data.put("data", result);
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/{customer_id}/balance/charge")
    public ResponseEntity<?> chargeBalance(@PathVariable("customer_id") Long customerId, @RequestBody ChargeRequest request) {
        if (request.balance() <= 0) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", false);
            errorResponse.put("message", "충전 금액은 0보다 커야합니다.");
            errorResponse.put("errorCode", "INVALID_CHARGE_AMOUNT");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (request.balance() % 10 != 0 ) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", false);
            errorResponse.put("message", "1원 단위는 충전할 수 없습니다.");
            errorResponse.put("errorCode", "INVALID_CHARGE_AMOUNT");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if(request.balance() < 500L || request.balance() > 100000L ){

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", false);
            errorResponse.put("message", "1회 충전 금액은 500원 이상 100,000원 이하여야 합니다.");
            errorResponse.put("errorCode", "INVALID_CHARGE_AMOUNT");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 고객 잔액 업데이트
        userBalance.put(customerId, userBalance.getOrDefault(customerId, 0L) + request.balance());

        Map<String, Object> response = new HashMap<>();
        response.put("result", true);
        response.put("message", "충전이 완료되었습니다.");
        response.put("data", userBalance.get(customerId));


        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
