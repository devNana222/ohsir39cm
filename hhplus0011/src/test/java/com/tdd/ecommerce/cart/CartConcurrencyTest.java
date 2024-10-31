package com.tdd.ecommerce.cart;

import com.tdd.ecommerce.cart.application.CartService;
import com.tdd.ecommerce.cart.application.dto.CartInfo;
import com.tdd.ecommerce.cart.domain.CartRepository;
import com.tdd.ecommerce.cart.domain.entity.Cart;
import com.tdd.ecommerce.customer.domain.Customer;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
@Sql(scripts = "/reset-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CartConcurrencyTest {

    @Autowired
    private CartService sut;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    Long customerId;
    @BeforeEach
    public void setUp() {
        Product product = new Product(1L, "Test Product", 10000L,"etc", null);
        Product product2 = new Product(2L, "Test Product2", 1000L,"etc", null);
        Long productId = productRepository.save(product).getProductId();
        Long productId2 = productRepository.save(product2).getProductId();

        productInventoryRepository.save(new ProductInventory(null, productId, 30L));
        productInventoryRepository.save(new ProductInventory(null, productId2, 30L));
        customerId = customerRepository.save(new Customer(null, 1000L, 0L)).getCustomerId();
    }

    @Test
    @DisplayName("한 고객의 카트에 동시에 들어가면 요청 개수 이상으로 들어갈 수 없다.")
    void addProductToCart() throws InterruptedException {
        int threadCnt = 5;

        List<CartInfo> cartInfos = List.of(new CartInfo(1L, 2L), new CartInfo(2L, 3L));

        AtomicInteger successCount = new AtomicInteger(0); // 성공 카운트 트래킹
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for(int i = 0; i < threadCnt; i++) {
            executorService.execute(() -> {
                try {
                    System.out.println("Executing addCartProducts with customerId: " + customerId + " and cartInfos: " + cartInfos);
                    sut.addCartProducts(customerId, cartInfos);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.err.println("요청 실패: " + e.getMessage());
                    e.printStackTrace(); // 예외의 전체 스택 트레이스를 출력하여 문제 위치와 원인을 추적
                } finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        executorService.shutdown();

        // then
        List<Cart> finalCartList = cartRepository.findAllByCustomerId(customerId);

        // 카트의 상품 수량이 중복 없이 처리되었는지 확인
        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failureCount.get());


        assertEquals(successCount.get(), threadCnt); // 모든 요청이 성공했는지 확인

        assertEquals(2, finalCartList.size()); // 2개의 상품이 중복 없이 추가되었는지 확인
        assertEquals(2 * threadCnt, finalCartList.get(0).getAmount()); // 상품 1의 수량이 올바른지 확인
        assertEquals(3 * threadCnt, finalCartList.get(1).getAmount()); // 상품 2의 수량이 올바른지 확인
    }
}
