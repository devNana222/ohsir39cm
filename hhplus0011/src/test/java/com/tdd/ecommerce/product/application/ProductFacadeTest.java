package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.product.domain.ProductInfoDto;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductFacadeTest {

    @Autowired
    private ProductFacade sut;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository; // 실제 Repository를 사용하는 경우

    @SpyBean
    ProductRepository spyProductRepository;
    @SpyBean
    ProductInventoryRepository spyProductInventoryRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void before(){
        cacheManager.getCache("getProductInfo").clear();

        Long productId1 = 1L;
        Long productId2 = 2L;

        ProductInventory productInventory1 = new ProductInventory(1L, productId1, 50L);
        ProductInventory productInventory2 = new ProductInventory(2L, productId2, 30L);

        // 데이터베이스에 제품 및 재고 저장
        spyProductRepository.save(new Product(productId1, "Test Product", 1000L, "etc", null));
        spyProductRepository.save(new Product(productId2, "Test Product2", 20000L, "etc", null));
        productInventoryRepository.save(productInventory1);
        productInventoryRepository.save(productInventory2);
    }


    @Test
    @DisplayName("🟢상품 조회 시 캐시가 적용된다.")
    void validateProductCache() {
        Long productId1 = 1L;
        Long productId2 = 2L;

        productService.getProductById(productId1);
        verify(spyProductRepository, times(1)).findByProductId(any()); // DB에서 호출

        productService.getProductById(productId1);
        verify(spyProductRepository, times(1)).findByProductId(any()); // DB 호출되지 않음

        productService.getProductById(productId2);
        verify(spyProductRepository, times(2)).findByProductId(any()); // DB에서 호출

    }


    @Test
    @DisplayName("🟢상품코드를 통해 상품 정보를 가져온다.")
    public void getProductByProductIdTest() {

        Long productId = 1L;

        ProductInfo result = sut.getProductByProductId(productId);

        System.out.println("result : " + result);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals("Test Product", result.getProductName());
        assertEquals(Optional.of(1000L), Optional.ofNullable(result.getPrice()));
        assertEquals(Optional.of(50L), Optional.of(result.getAmount()));

    }

    @Test
    @DisplayName("🟢전체 상품 정보를 가져온다.")
    public void getProducts_ShouldReturnListOfProductInfo() {
        List<ProductInfo> result = sut.getProducts();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(Optional.of(1L), Optional.ofNullable(result.get(0).getProductId()));
        assertEquals("Test Product", result.get(0).getProductName());

        assertEquals(Optional.of(1000L), Optional.ofNullable(result.get(1).getPrice()));
        assertEquals(Optional.of(30L), Optional.ofNullable(result.get(1).getAmount()));

    }
}