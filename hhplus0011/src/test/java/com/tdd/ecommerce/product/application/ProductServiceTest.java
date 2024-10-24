package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.tdd.ecommerce.common.exception.ECommerceExceptions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductInventoryRepository productInventoryRepository;

    @InjectMocks
    private ProductService productService;


    @Test
    @DisplayName("🟢상품 코드로 상품 조회하면 상품은 하나가 나오고 상품의 재고는 20개가 반환된다.")
    void getProductByProductId() {
        //given
        Product product = new Product(1L, "있는 상품", 100000L, "etc", null);
        ProductInventory inventory = new ProductInventory(1L, 1L, 20L);

        //when
        when(productRepository.findByProductId(1L)).thenReturn(product);
        when(productInventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        List<ProductServiceResponse> response = productService.getProductByProductId(1L);

        //then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(20L, response.getFirst().getAmount());
    }

    @Test
    @DisplayName("🔴 유효하지 않은 상품 조회하면 INVALID_PRODUCT Exception이 발생한다.")
    void getProductByProductId_INVALIDPRODUCT() {
        //when
        when(productRepository.findByProductId(1L)).thenReturn(null);

        //then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.getProductByProductId(1L);
        });

        assertEquals(INVALID_PRODUCT.getMessage(), exception.getMessage());
        assertEquals(INVALID_PRODUCT.getStatus(), exception.getErrorCode().getStatus());
    }

    @Test
    @DisplayName("🔴 재고 없는 상품 조회하면 OUT_OF_STOCK Exception이 발생한다.")
    void getProductByProductId_OUTOFSTOCK() {
        // given
        Product product = new Product(1L, "재고 없는 상품", 100000L, "etc", null);
        ProductInventory inventory = new ProductInventory(1L, 1L, 0L);

        //when
        when(productRepository.findByProductId(1L)).thenReturn(product);
        when(productInventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        //then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.getProductByProductId(1L);
        });

        assertEquals(OUT_OF_STOCK.getMessage(), exception.getMessage());
        assertEquals(OUT_OF_STOCK.getStatus(), exception.getErrorCode().getStatus());
    }

    @Test
    @DisplayName("🟢재고가 있는 상품 1L만 조회된다.")
    void getProducts() {
        // given
        List<Product> products = List.of(
                new Product(1L, "재고있는상품", 5000L, "etc", null),
                new Product(2L, "재고없는상품", 3000L, "etc", null)
        );
        List<ProductInventory> inventories = List.of(
                new ProductInventory(1L, 1L, 100L) // 재고가 있는 상품만 추가
        );

        // when
        when(productInventoryRepository.findProductsByAmountGreaterThanZero()).thenReturn(inventories);
        when(productRepository.findByProductId(1L)).thenReturn(products.getFirst()); // 재고가 있는 상품

        List<ProductServiceResponse> response = productService.getProducts();

        // then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("재고있는상품", response.getFirst().getProductName());
    }
}