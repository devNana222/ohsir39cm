package com.tdd.ecommerce.product;

import com.tdd.ecommerce.product.dto.ProductInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/stock")
public class ProductStockController {

    private static final List<ProductInfo> stocks = List.of(
            new ProductInfo(1L, "홈즈 양면 홈바형 생활발수 2인용 접이식 패브릭 소파베드(쿠션증정)", 229000L ,100L),
            new ProductInfo(2L, "콤마 기능성 아쿠아 2인 패브릭 소파 (스툴 미포함)", 179000L,30L),
            new ProductInfo(3L, "미아 3인 라운지 패브릭 소파 베드", 399900L, 58L)
    );

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductInfo(@PathVariable Long productId) {
        Optional<ProductInfo> selectedItem = stocks.stream()
                .filter(product -> product.productId().equals(productId))
                .findFirst();

        if (selectedItem.isPresent()) {
            Map<String, Object> item = new HashMap<>();
            ProductInfo productInfo = selectedItem.get();

            item.put("상품코드", productInfo.productId());
            item.put("상품명", productInfo.productName());
            item.put("가격", productInfo.price());
            item.put("재고수량", productInfo.stock());

            Map<String, Object> data = new HashMap<>();
            data.put("result", true);
            data.put("message", "조회성공");
            data.put("data", item);

            return ResponseEntity.ok(data);
        }
        else{
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("result", false);
            errorResponse.put("message", "없는 상품");
            errorResponse.put("errorCode", "INVALID_PRODUCT");

            return ResponseEntity.status(400).body(errorResponse);
        }
    }

}
