package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.product.domain.ProductInfoDto;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
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
    private ProductService spyProductService;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;


    @Test
    @DisplayName("🟢상품 조회 시 캐시가 적용된다.")
    public void validateProductCache2(){
        Long productId = 1L;
        ProductInfoDto product = new ProductInfoDto(1L, "Test Product", 100000L, "etc");
        ProductInventory productInventory = new ProductInventory(1L, productId, 50L);

        when(productService.getProductById(productId)).thenReturn(product);
        when(productService.getProductInventoryById(productId)).thenReturn(Optional.of(productInventory));

        sut.getProductByProductId(productId);
        verify(productService, times(1)).getProductById(productId);

        sut.getProductByProductId(productId);
        verify(productService, times(1)).getProductById(productId);

        ProductInfoDto product2 = new ProductInfoDto(2L, "Test Product2", 100000L, "etc");
        when(productService.getProductById(2L)).thenReturn(product2);

        //다른 productId를 넣으면 캐시가 신규로 생성된다.
        sut.getProductByProductId(2L);

        verify(productService, times(2)).getProductById(2L);

    }

    @Test
    @DisplayName("🟢상품 조회 시 캐시가 적용된다.")
    void validateProductCache() {
        Long productId1 = 1L;
        Long productId2 = 2L;

        // 초기 데이터 준비 (데이터베이스나 실제 서비스에서 데이터를 조회)
        Product product1 = new Product(1L, "Test Product", 100000L, "etc", null);
        ProductInventory productInventory1 = new ProductInventory(1L, productId1, 50L);
        Product product2 = new Product(2L, "Test Product2", 100000L, "etc", null);
        ProductInventory productInventory2 = new ProductInventory(2L, productId2, 50L);

        // 실제 데이터를 데이터베이스에 저장 (예시)
        productRepository.save(product1);
        productRepository.save(product2);

        productInventoryRepository.save(productInventory1);
        productInventoryRepository.save(productInventory2);

        // 캐시가 적용된 상태에서 상품 조회
        sut.getProductByProductId(productId1);
        sut.getProductByProductId(productId1); // 두 번째 호출에서 캐시 사용 여부 확인

        verify(productService, times(1)).getProductById(productId1); // 첫 번째 호출만 이루어져야 함
        verify(productService, times(1)).getProductById(productId2); // 두 번째 productId 호출

    }

    /*
    @Test
    @DisplayName("🟢상품코드를 통해 상품 정보를 가져온다.")
    public void getProductByProductIdTest() {

        Long productId = 1L;
        Product product = new Product(1L, "Test Product", 100000L, "etc", null);
        ProductInventory productInventory = new ProductInventory(1L, productId, 50L);

        when(productService.getProductById(productId)).thenReturn(product);
        when(productService.getProductInventoryById(productId)).thenReturn(Optional.of(productInventory));


        ProductInfo result = sut.getProductByProductId(productId);


        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals("Test Product", result.getProductName());
        assertEquals(1000L, result.getPrice());
        assertEquals(50L, result.getAmount());

        verify(productService).getProductById(productId);
        verify(productService).getProductInventoryById(productId);
    }

    @Test
    public void getProducts_ShouldReturnListOfProductInfo() {
        // Arrange
        List<ProductServiceResponse> products = Arrays.asList(
                new ProductServiceResponse(1L, "Product 1", "Category 1", 1000L, 10L),
                new ProductServiceResponse(2L, "Product 2", "Category 2", 2000L, 20L)
        );

        when(productService.getProducts()).thenReturn(products);

        // Act
        List<ProductInfo> result = sut.getProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getProductId());
        assertEquals("Product 1", result.get(0).getProductName());
        assertEquals(1000L, result.get(0).getPrice());
        assertEquals(10L, result.get(0).getAmount());

        assertEquals(2L, result.get(1).getProductId());
        assertEquals("Product 2", result.get(1).getProductName());
        assertEquals(2000L, result.get(1).getPrice());
        assertEquals(20L, result.get(1).getAmount());

        verify(productService).getProducts();
    }

*/
}