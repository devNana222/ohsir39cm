package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.event.domain.RankingRepository;
import com.tdd.ecommerce.order.domain.Order;
import com.tdd.ecommerce.order.domain.OrderProduct;
import com.tdd.ecommerce.product.domain.ProductInfoDto;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableCaching
public class ProductFacadeTest2 {
    @Autowired
    private ProductFacade sut;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductInventoryRepository productInventoryRepository;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void before(){
        cacheManager.getCache("getProductInfo").clear();
    }

    @Test
    @DisplayName("🟢상품 조회 시 캐시가 적용된다.")
    void validateProductCache() {
        Long productId1 = 1L;
        Long productId2 = 2L;

        ProductInventory productInventory1 = new ProductInventory(1L, productId1, 50L);
        ProductInventory productInventory2 = new ProductInventory(2L, productId2, 30L);

        // 데이터베이스에 제품 및 재고 저장
        productRepository.save(new Product(productId1, "Test Product", 100000L, "etc", null));
        productRepository.save(new Product(productId2, "Test Product2", 100000L, "etc", null));
        productInventoryRepository.save(productInventory1);
        productInventoryRepository.save(productInventory2);


        sut.getProductByProductId(productId1);
        ProductInfoDto productInfoDto = productService.getProductById(productId1);
        Long productId = productInfoDto.getProductId();
        System.out.println("productId : "+productId);
        Product product = productRepository.findByProductId(productId1);
        System.out.println("product return :  " + product);
        System.out.println("info DTO Return : " + productInfoDto);

        verify(productService, times(1)).getProductById(productId1); // DB에서 호출

        sut.getProductByProductId(productId1);
        verify(productService, times(1)).getProductById(productId1); // DB 호출되지 않음

        sut.getProductByProductId(productId2);
        verify(productService, times(1)).getProductById(productId2); // DB에서 호출

    }
}
