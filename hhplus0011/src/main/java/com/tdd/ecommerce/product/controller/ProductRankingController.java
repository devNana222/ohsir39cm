package com.tdd.ecommerce.product.controller;

import com.tdd.ecommerce.order.dto.OrderHistory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rank")
public class ProductRankingController {

    private static final List<OrderHistory> orders = List.of(
            new OrderHistory(1L, 30L, "2024-10-07"),
            new OrderHistory(2L, 100L, "2024-10-08"),
            new OrderHistory(4L, 30L, "2024-10-07"),
            new OrderHistory(5L, 100L, "2024-10-08"),
            new OrderHistory(3L, 17L, "2024-10-10")
    );

    @GetMapping("/{today}")
    public ResponseEntity<?> getTopRank(@PathVariable("today") String today) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try{
            dateFormat.parse(today);
        } catch (ParseException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", false);
            errorResponse.put("message", "잘못된 날짜 입력.");
            errorResponse.put("errorCode", "INVALID_DATE");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("result", true);
        data.put("message", "조회성공");
        data.put("data", orders);

        return ResponseEntity.ok().body(data);
    }
}
