package com.tdd.ecommerce.product.presentation;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.common.model.CommonApiResponse;
import com.tdd.ecommerce.common.model.ResponseUtil;
import com.tdd.ecommerce.product.application.ProductService;
import com.tdd.ecommerce.product.application.ProductServiceResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(
        name = "상품 관리 시스템",
        description = "상품 번호로 상품을 조회할 수 있습니다. " +
                "pthVariable로 productId가 없으면 재고가 있는 전체 상품을 조회합니다."
)
@RestController
@RequestMapping("/products")
@Slf4j
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
            log.info("상품 코드 : {}, 상품 정보 : {}", productId, products);
            return ResponseUtil.buildSuccessResponse("상품코드 : "+ productId + "의 상품 정보입니다.", products);
        } catch (BusinessException e) {
            log.error(ECommerceExceptions.INVALID_PRODUCT.getMessage());
            return ResponseUtil.buildErrorResponse(ECommerceExceptions.INVALID_PRODUCT, ECommerceExceptions.INVALID_PRODUCT.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<CommonApiResponse<?>> getProductsInStock() {
        try{
            List<ProductServiceResponse> products = productService.getProducts();

            return ResponseUtil.buildSuccessResponse("현재 재고가 있는 상품들입니다.", products);
        }
        catch(BusinessException e){
            log.error(ECommerceExceptions.OUT_OF_STOCK.getMessage());
            return ResponseUtil.buildErrorResponse(ECommerceExceptions.OUT_OF_STOCK, ECommerceExceptions.OUT_OF_STOCK.getMessage());
        }
    }
}
