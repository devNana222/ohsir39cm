package com.tdd.ecommerce.order.presentation;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.common.model.ResponseUtil;
import com.tdd.ecommerce.order.application.OrderService;
import com.tdd.ecommerce.order.application.OrderServiceResponse;
import com.tdd.ecommerce.order.presentation.dto.OrderRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        try{
            List<OrderServiceResponse> serviceResponses = orderService.createOrder(request.getCustomerId(), request.getOrderProducts());

            return ResponseUtil.buildSuccessResponse("주문을 성공하였습니다.🍀 주문정보를 확인하세요.", serviceResponses);
        }catch(BusinessException e){
            return ResponseUtil.buildErrorResponse(ECommerceExceptions.FAILED_ORDER, ECommerceExceptions.FAILED_ORDER.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {

        List<OrderServiceResponse> serviceResponses = orderService.getOrderList(orderId);

        if(serviceResponses.isEmpty()){
            return ResponseUtil.buildErrorResponse(ECommerceExceptions.INVALID_ORDER, ECommerceExceptions.INVALID_ORDER.getMessage());
        }
        return ResponseUtil.buildSuccessResponse("주문번호 : "+orderId + "의 주문정보입니다.", serviceResponses);
    }

    @PostMapping("/cart")
    public ResponseEntity<?> createOrderFromCart(@RequestBody OrderRequest request) {
        List<OrderServiceResponse> serviceResponses = orderService.createOrderFromCart(request.getCustomerId(), request.getOrderProducts());
        return ResponseUtil.buildSuccessResponse("장바구니에서 주문을 정상적으로 전송하였습니다.", serviceResponses);
    }
}