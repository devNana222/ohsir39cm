package com.tdd.ecommerce.product.presentation;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ErrorCode;
import com.tdd.ecommerce.common.model.CommonApiResponse;
import com.tdd.ecommerce.product.application.ProductService;
import com.tdd.ecommerce.product.application.ProductServiceResponse;
import com.tdd.ecommerce.product.domain.ProductInfoDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Tag(
        name = "상품 관리 시스템",
        description = "상품 번호로 상품을 조회할 수 있습니다. " +
                "pthVariable로 productId가 없으면 재고가 있는 전체 상품을 조회합니다."
)
@RestController
@RequestMapping("/products")
public class ProductStockController {

    private final ProductService productService;

    @Autowired
    public ProductStockController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CommonApiResponse<?>> getProductInfo(@PathVariable Long productId) {
        try{
            List<ProductServiceResponse> products = productService.getProductsByProductId(productId);

            CommonApiResponse<?> response = new CommonApiResponse<>(true, "조회 성공", products);
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            CommonApiResponse<ErrorCode> errorResponse = new CommonApiResponse<>(false, e.getMessage(), e.getErrorCode());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping()
    public ResponseEntity<CommonApiResponse<?>> getProductsInStock() {
        try{
            List<ProductServiceResponse> products = productService.getProducts();

            CommonApiResponse<?> response = new CommonApiResponse<>(true, "현재 재고가 있는 상품들입니다.", products);
            return ResponseEntity.ok(response);
        }
        catch(BusinessException e){
            CommonApiResponse<ErrorCode> errorResponse = new CommonApiResponse<>(false, e.getMessage(), e.getErrorCode());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
