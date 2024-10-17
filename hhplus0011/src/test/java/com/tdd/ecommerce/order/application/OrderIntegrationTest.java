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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderIntegrationTest {
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



    private Long customerId;
    private Long productId;
    private Long inventoryId;

    @BeforeEach
    public void setUp() {

        Customer customer = new Customer(null, 100000L); // 초기 잔액 100,000
        customerId = customerRepository.save(customer).getCustomerId();

        Product product = new Product(1L, "Test Product", 10000L,"etc", null); // 가격 10,000
        productId = productRepository.save(product).getProductId();

        ProductInventory inventory = new ProductInventory(inventoryId, productId, 100L); // 재고 100

        inventoryId = productInventoryRepository.save(inventory).getId(); // 재고 ID 저장
    }
 /*
    @BeforeEach
    void setUp() {
        productInventoryRepository.updateStock(1L, 30L);
        orderProductRepository.deleteByProductId(1L);
    }
*/
    @Test
    @Rollback
    @DisplayName("🟢주문 테스트 성공")
    void createOrder_SUCCESS(){
        Long orderId = saveOrder(customerId).getOrderId();
        OrderProduct orderProduct = new OrderProduct(null, orderId, productId, 1L, 10000L  ); // 상품 ID와 수량
        List<OrderProduct> orders = Collections.singletonList(orderProduct);

        List<OrderServiceResponse> response = sut.createOrder(customerId, orders);

        System.out.println("res : " + response);
        // 응답 검증
        assertThat(response).isNotEmpty();
        assertThat(response.getFirst().getOrderProducts().getFirst().getProductId()).isEqualTo(productId);
        assertThat(response.getFirst().getOrderProducts().getFirst().getAmount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("🟢주문 정보 가져오기")
    void getOrderList_SUCCESS(){
        Long orderId = saveOrder(customerId).getOrderId();

        List<OrderServiceResponse> response = sut.getOrderList(customerId);
        assertThat(response).isNotEmpty();
        assertThat(response.getFirst().getOrderProducts().getFirst().getProductId()).isEqualTo(productId);
    }
  /*  @Test
    @DisplayName("🔴주문 동시성 테스트 - 재고 초과")
    void createOrder_FAIL() throws InterruptedException {
        Long productId = 1L;
        int quantity = 20;


        CountDownLatch latch = new CountDownLatch(quantity);

        ExecutorService executorService = Executors.newFixedThreadPool(quantity);

        for(int i = 1; i < quantity + 1; i++){
            Long  uniqueId = (long) i;
            executorService.submit(()->{
                try{
                    List<OrderProduct> orderProducts = new ArrayList<>();
                    Long orderId = saveOrder(uniqueId).getOrderId();

                    orderProducts.add(new OrderProduct(null, orderId, productId, 2L, 1000L));
                    sut.createOrder(uniqueId, orderProducts);
                }catch (BusinessException e){
                    System.out.println(" e.getMessage() " + "customerId : " + uniqueId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(); // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown();

        Thread.sleep(1000);

        assertEquals(orderProductRepository.countByProductId(productId), 15L);
    }
*/
    private Order saveOrder(Long customerId) {
        Order order = new Order(null, customerId);
        return orderRepository.save(order);
    }

}
