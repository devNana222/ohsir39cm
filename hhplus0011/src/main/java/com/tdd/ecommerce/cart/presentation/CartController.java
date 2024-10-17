package com.tdd.ecommerce.cart.presentation;

import com.tdd.ecommerce.cart.application.dto.CartResponse;
import com.tdd.ecommerce.cart.application.CartService;
import com.tdd.ecommerce.cart.presentation.dto.CartRequestDto;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.common.model.CommonApiResponse;

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
    public ResponseEntity<CommonApiResponse<?>> getCart(@PathVariable("customerId") long customerId) {
        List<CartResponse> cartProducts = cartService.getCartProducts(customerId);

        CommonApiResponse<?> response = new CommonApiResponse<>(true, "조회 성공", cartProducts);
        return ResponseEntity.ok(response);
    }

    @PatchMapping()
    public ResponseEntity<CommonApiResponse<?>> addCart(@RequestBody CartRequestDto request) {
        CartResponse cartProducts = cartService.addCartProducts(request.customerId(), request.products());

        CommonApiResponse<?> response = new CommonApiResponse<>(true, "장바구니에 정상적으로 담겼습니다.", cartProducts);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> removeProduct(@PathVariable("customerId") long customerId) {
        boolean result = cartService.removeCart(customerId);
        String resultMsg = "";

        if(result){
            resultMsg = "장바구니가 정상적으로 삭제되었습니다.";
        }
        else{
            resultMsg = ECommerceExceptions.INVALID_CART.getMessage();
        }
        CommonApiResponse<?> response = new CommonApiResponse<>(result, resultMsg, null);
        return ResponseEntity.ok(response);

    }

}
