package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService sut;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @SpyBean
    private ProductService spyProductService;

    @SpyBean
    private ProductInventoryRepository spyProductInventoryRepository;
    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        //재고 있는 상품
        Long productId = productRepository.save(new Product(1L, "Test Product", 10000L, "etc", null)).getProductId();
        productInventoryRepository.save(new ProductInventory(null, productId, 100L));

        //재고 없는 상품
        Long productId2 = productRepository.save(new Product(2L, "Test Product", 10000L, "etc", null)).getProductId();
        productInventoryRepository.save(new ProductInventory(null, productId2, 0L));

    }


    @Test
    @DisplayName("🟢상품 번호1을 입력하면 상품번호 1번에 대한 정보 하나를 가져오고, 가져온 정보의 상품번호도 1번이다.")
    void getProductByProductId_SUCCESS() throws Exception {
        Long productId = 1L;

        ProductInfoDto productServiceResponses = sut.getProductById(productId);

        assertThat(productServiceResponses.getProductId()).isEqualTo(productId);

    }

    @Test
    @DisplayName("🔴없는 상품 조회하면 INVALID_PRODUCT Exception이 발생한다.")
    void getProductByProductId_FAIL() throws Exception {
        Long productId = 100L;

        assertThatThrownBy(() -> sut.getProductById(productId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ECommerceExceptions.INVALID_PRODUCT.getMessage());
    }

    @Test
    @DisplayName("🔴재고 없는 상품 조회하면 OUT_OF_STOCK Exception이 발생한다. ")
    void getProduct_OUT_OF_STOCK() throws Exception {
        //given
        Long productId = 2L;
        //when&then
        assertThatThrownBy(() -> sut.getProductInventoryById(productId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ECommerceExceptions.OUT_OF_STOCK.getMessage());
    }

    @Test
    @DisplayName("🟢getProducts를 조회하면 현재 재고가 있는 상품만 조회한다.")
    void getProducts_SUCCESS() throws Exception {
        //given
        //when
        List<ProductServiceResponse> responses = sut.getProducts();

        //then
        assertEquals(1, responses.size());
    }
}

