package com.tdd.ecommerce.order.presentation;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.common.model.CommonApiResponse;
import com.tdd.ecommerce.order.application.OrderService;
import com.tdd.ecommerce.order.application.OrderServiceResponse;
import com.tdd.ecommerce.order.presentation.dto.OrderRequest;
import com.tdd.ecommerce.order.presentation.dto.OrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(
        name = "주문&결제 시스템",
        description = "주문 + 결제 후 외부 시스템에 결과를 전송합니다. " +
                "주문번호로 주문정보를 가져옵니다."
)
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<CommonApiResponse<?>> createOrder(@RequestBody OrderRequest request) {
        try{
            List<OrderServiceResponse> serviceResponses = orderService.createOrder(request.getCustomerId(), request.getOrderProducts());
            CommonApiResponse<List<OrderServiceResponse>> response = new CommonApiResponse<>(true, "주문을 성공하였습니다.🍀 주문정보를 확인하세요.", serviceResponses);

            return ResponseEntity.ok(response);
        }catch(BusinessException e){
            CommonApiResponse<ECommerceExceptions> errorResponse = new CommonApiResponse<>(false, e.getMessage(), ECommerceExceptions.FAILED_ORDER);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonApiResponse<?>> getOrder(@PathVariable Long orderId) {

        List<OrderServiceResponse> serviceResponses = orderService.getOrderList(orderId);

        if(serviceResponses.isEmpty()){
            CommonApiResponse<ECommerceExceptions> errorResponse = new CommonApiResponse<>(false, "주문 정보를 찾을 수 없습니다.", null);

            return ResponseEntity.badRequest().body(errorResponse);
        }

        CommonApiResponse<List<OrderServiceResponse>> response = new CommonApiResponse<>(true, "주문 정보입니다.", serviceResponses);

        return ResponseEntity.ok(response);

    }

}