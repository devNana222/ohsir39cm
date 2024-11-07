package com.tdd.ecommerce.product.presentation;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.common.model.CommonApiResponse;
import com.tdd.ecommerce.common.model.ResponseUtil;
import com.tdd.ecommerce.product.application.ProductFacade;
import com.tdd.ecommerce.product.application.ProductInfo;
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

    private final ProductFacade productFacade;

    @Autowired
    public ProductStockController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductInfo(@PathVariable Long productId) {
        try{
            ProductInfo products = productFacade.getProductByProductId(productId);
            log.info("상품 코드 : {}, 상품 정보 : {}", productId, products);

            return ResponseUtil.buildSuccessResponse("상품코드 : "+ productId + "의 상품 정보입니다.", products);
        } catch (BusinessException e) {
            log.warn(ECommerceExceptions.INVALID_PRODUCT.getMessage());
            return ResponseUtil.buildErrorResponse(ECommerceExceptions.INVALID_PRODUCT, ECommerceExceptions.INVALID_PRODUCT.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getProductsInStock() {
        try{
            List<ProductInfo> products = productFacade.getProducts();

            return ResponseUtil.buildSuccessResponse("현재 재고가 있는 상품들입니다.", products);
        }
        catch(BusinessException e){
            log.warn(ECommerceExceptions.OUT_OF_STOCK.getMessage());
            return ResponseUtil.buildErrorResponse(ECommerceExceptions.OUT_OF_STOCK, ECommerceExceptions.OUT_OF_STOCK.getMessage());
        }
    }
}
