package com.tdd.ecommerce.cart;

import com.tdd.ecommerce.cart.dto.Cart;
import com.tdd.ecommerce.order.dto.OrderItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static List<Cart> carts = new ArrayList<>(List.of(
            new Cart(1L, 1L, 3L, 3L),
            new Cart(2L, 1L, 1L, 5L),
            new Cart(3L, 1L, 2L, 1L),
            new Cart(4L, 2L, 4L, 10L)
    ));

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable("cartId") long cartId) {
        List<Cart> selectedItem = carts.stream()
                .filter(cart -> cart.userCartId().equals(cartId))
                .toList();

        if (selectedItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else{
            List<Map<String, Object>> items = new ArrayList<>();

            for(Cart cartItem : selectedItem) {

                Map<String, Object> item = new HashMap<>();
                item.put("장바구니ID", cartItem.userCartId());
                item.put("상품코드", cartItem.productId());
                item.put("수량", cartItem.amount().toString());

                items.add(item);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("result", true);
            data.put("message", "조회성공");
            data.put("data", items);
            return ResponseEntity.ok().body(data);
        }
    }

    @PostMapping("/{cartId}/add")
    public ResponseEntity<?> addProduct(@PathVariable("cartId") long cartId, @RequestBody OrderItem orderInfo) {

        if(orderInfo.amount() <= 0){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", false);
            errorResponse.put("message", "정확한 수량을 입력해주세요.");
            errorResponse.put("errorCode", "INVALID_AMOUNT");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        Optional<Cart> existingProduct = carts.stream()
                .filter(product -> product.productId().equals(orderInfo.productId()))
                .findFirst();

        Map<String, Object> item = new HashMap<>();

        Map<String, Object> data = new HashMap<>();

        if (existingProduct.isEmpty()) {
            carts.add(new Cart(5L, cartId, orderInfo.productId(), orderInfo.amount()));
            item.put("상품코드", orderInfo.productId());
            item.put("개수", orderInfo.amount());

            data.put("data", item);
            data.put("result", true);
            data.put("message", "추가 성공");

            return ResponseEntity.ok().body(data);
        }
        else{
            Cart cartItem = existingProduct.get();
            Long updatedQuantity = cartItem.amount() + orderInfo.amount();

            carts.remove(cartItem);
            carts.add(new Cart(5L, cartId, orderInfo.productId(), updatedQuantity));

            item.put("상품코드", orderInfo.productId());
            item.put("개수", updatedQuantity);

            data.put("data", item);
            data.put("result", true);
            data.put("message", "추가 성공");
            return ResponseEntity.ok().body(data);
        }
    }

    @PostMapping("/{cartId}/remove")
    public ResponseEntity<?> removeProduct(@PathVariable("cartId") long cartId, @RequestBody OrderItem orderInfo) {
        Optional<Cart> existingProduct = carts.stream()
                .filter(product -> product.productId().equals(orderInfo.productId()))
                .findFirst();

        if (existingProduct.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", false);
            errorResponse.put("message", "장바구니에 존재하지 않는 상품은 삭제할 수 없습니다.");
            errorResponse.put("errorCode", "INVALID_PRODUCT");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        Cart cartItem = existingProduct.get();
        Long updatedQuantity = cartItem.amount() - orderInfo.amount();

        List<Map<String, Object>> items = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("장바구니ID", cartItem.userCartId());
        item.put("상품코드", cartItem.productId());

        if (updatedQuantity < 0) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", false);
            errorResponse.put("message", "장바구니에 담긴 수량보다 삭제수량이 더 많습니다.");
            errorResponse.put("errorCode", "INVALID_PRODUCT");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        carts.remove(cartItem);
        carts.add(new Cart(5L, cartId, orderInfo.productId(), updatedQuantity));

        item.put("수량", updatedQuantity.toString());

        items.add(item);

        if(updatedQuantity == 0){
            Map<String, Object> data = new HashMap<>();
            data.put("result", true);
            data.put("message", "삭제 성공");
            data.put("data", items);
            return ResponseEntity.ok().body(data);
        }
        else{
            Map<String, Object> data = new HashMap<>();
            data.put("result", true);
            data.put("message", "수량이 변경되었습니다.");
            data.put("data", items);
            return ResponseEntity.ok().body(data);
        }
    }

}
