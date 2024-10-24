package com.tdd.ecommerce.cart.presentation;

import com.tdd.ecommerce.cart.application.dto.CartResponse;
import com.tdd.ecommerce.cart.application.CartService;
import com.tdd.ecommerce.cart.presentation.dto.CartRequest;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;

import com.tdd.ecommerce.common.model.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "장바구니 시스템",
        description = "장바구니 조회 / 상품추가 및 삭제 / 장바구니 전체 삭제"
)
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCart(@PathVariable("customerId") long customerId) {
        List<CartResponse> cartProducts = cartService.getCartProducts(customerId);
        return ResponseUtil.buildSuccessResponse("현재 장바구니 정보입니다.", cartProducts);
    }

    @PatchMapping()
    public ResponseEntity<?> addCart(@RequestBody CartRequest request) {
        CartResponse cartProducts = cartService.addCartProducts(request.customerId(), request.products());

        return ResponseUtil.buildSuccessResponse("장바구니에 정상적으로 담겼습니다.", cartProducts);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> removeProduct(@PathVariable("customerId") long customerId) {
        boolean result = cartService.removeCart(customerId);

        if(result){
            return ResponseUtil.buildSuccessResponse("장바구니에 정상적으로 담겼습니다.", null);
        }
        else{
            return ResponseUtil.buildErrorResponse(ECommerceExceptions.FAILED_DELETE, ECommerceExceptions.FAILED_DELETE.getMessage());
        }

    }

}
