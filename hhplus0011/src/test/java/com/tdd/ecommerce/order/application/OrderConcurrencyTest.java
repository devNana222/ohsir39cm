package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.infrastructure.Customer;
import com.tdd.ecommerce.order.domain.OrderProductRepository;
import com.tdd.ecommerce.order.domain.OrderRepository;
import com.tdd.ecommerce.order.infrastructure.Order;
import com.tdd.ecommerce.order.infrastructure.OrderProduct;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;

import com.tdd.ecommerce.product.infrastructure.Product;
import com.tdd.ecommerce.product.infrastructure.ProductInventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
@AutoConfigureMockMvc
@Sql(scripts = "/reset-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class OrderConcurrencyTest {
    @Autowired
    private OrderService sut;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductInventoryRepository productInventoryRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("🔴주문 동시성 테스트 - 40개 동시요청했으나 재고가 30개여서 10개는 실패")
    void createOrder_FAIL() throws InterruptedException {
        Long stockAmount = 30L;
        int quantity = 40;

        //상품 만들기
        Long productId = insertProduct("test 상품", 100L, "etc").getProductId();
        insertProductInventory(productId, stockAmount);

        for(int i = 0; i < quantity; i++) {
            createCustomer(1000L);
        }

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(quantity);

        ExecutorService executorService = Executors.newFixedThreadPool(quantity);

        for(int i = 1; i < quantity + 1; i++){
            Long  uniqueId = (long) i;
            executorService.submit(()->{
                try{
                    List<OrderProduct> orderProducts = new ArrayList<>();
                    Long orderId = saveOrder(uniqueId).getOrderId();

                    orderProducts.add(new OrderProduct(null, orderId, productId, 1L, 1000L));

                    sut.createOrder(uniqueId, orderProducts);
                    successCount.incrementAndGet();
                }catch (BusinessException e){
                    failureCount.incrementAndGet();
                    System.out.println(" e.getMessage() : " + e.getMessage() + " customerId : " + uniqueId);
                } finally {
                    System.out.println(" success customerId : " + uniqueId);
                    latch.countDown();
                }
            });
        }
        latch.await(); // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown();

        Thread.sleep(1000);
        assertEquals(stockAmount, successCount.get());
    }
    private Order saveOrder(Long customerId) {
        Order order = new Order(null, customerId);
        return orderRepository.save(order);
    }

    private Product insertProduct(String productName, Long price, String category) {
        return productRepository.save(Product.builder()
                .productName(productName)
                .price(price)
                .category(category)
                .build());
    }

    private void insertProductInventory(Long productId, Long quantity) {
        productInventoryRepository.save(ProductInventory.builder()
                .productId(productId)
                .amount(quantity)
                .build());
    }

    private void createCustomer(Long balance){
        customerRepository.save(Customer.builder()
                .balance(balance)
                .build());
    }
}
