package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceException;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.infrastructure.Product;
import com.tdd.ecommerce.product.infrastructure.ProductInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductIntegrationTest {

    @Autowired
    private ProductService sut;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    private Long customerId;
    private Long productId;
    private Long inventoryId;

    @BeforeEach
    void setUp() {
        Product product = new Product(1L, "Test Product", 10000L,"etc", null); // 가격 10,000
        productId = productRepository.save(product).getProductId();

        ProductInventory inventory = new ProductInventory(inventoryId, productId, 100L); // 재고 100
        inventoryId = productInventoryRepository.save(inventory).getId();
    }

    @Test
    @DisplayName("🟢상품 번호로 상품정보 가져오기")
    void getProductByProductId_SUCCESS() throws Exception {
        Long productId = 1L;

        List<ProductServiceResponse> productServiceResponses = sut.getProductsByProductId(productId);

        assertThat(productServiceResponses.size()).isEqualTo(1);
        assertThat(productServiceResponses.get(0).getProductId()).isEqualTo(productId);

    }

    @Test
    @DisplayName("🔴없는 상품 조회")
    void getProductByProductId_FAIL() throws Exception {
        Long productId = 100L;

        assertThatThrownBy(() -> sut.getProductsByProductId(productId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ECommerceException.INVALID_PRODUCT.getMessage());
    }

    @Test
    @DisplayName("🔴재고 없는 상품 조회")
    void getProduct_OUT_OF_STOCK() throws Exception {
        Long amount = 0L;

        Product product = new Product(null, "테스트상품", 100L, "etc", null);
        Long savedProductId = saveProduct(product).getProductId();

        ProductInventory inventory = new ProductInventory(null, savedProductId,amount);
        saveProductInventory(inventory);

        assertThatThrownBy(() -> sut.getProductsByProductId(savedProductId).getFirst())
                .isInstanceOf(BusinessException.class)
                .hasMessage(ECommerceException.OUT_OF_STOCK.getMessage());

    }

    @Test
    @DisplayName("🟢재고가 있는 전체 상품 조회")
    void getProducts_SUCCESS() throws Exception {
        List<ProductServiceResponse> responses = sut.getProducts();

        assertTrue(responses.get(0).getAmount() > 0);
    }

    private Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    private ProductInventory saveProductInventory(ProductInventory productInventory) {
        return productInventoryRepository.save(productInventory);
    }
}
