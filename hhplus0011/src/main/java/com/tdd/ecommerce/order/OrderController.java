package com.tdd.ecommerce.order;

import com.tdd.ecommerce.order.dto.OrderItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        OrderResponse orderResponse = new OrderResponse(1L, request.customerId(), 1000L, LocalDateTime.now(), 3000L, 1L, "POINT", request.orderItems());
        Map<String, Object> data = new HashMap<>();
        data.put("result", true);
        data.put("message", "주문 완료");
        data.put("data", orderResponse);

        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        List<OrderItem> orderItems = new ArrayList<>(List.of(new OrderItem(1L, 3L, 5000L)));
        OrderResponse orderResponse = new OrderResponse(orderId, 1L, 300L, LocalDateTime.now(), 3000L, 1L, "CASH", orderItems);


        Map<String, Object> data = new HashMap<>();
        data.put("result", true);
        data.put("message", "주문 완료");
        data.put("data", orderResponse);

        return ResponseEntity.ok().body(data);
    }

    record OrderRequest(Long customerId, List<OrderItem> orderItems) {}
    record OrderResponse(Long orderId, Long customerId, Long remainBalance, LocalDateTime orderDate, Long totalPrice, Long status, String paymentMethod, List<OrderItem> orderItems){}

}