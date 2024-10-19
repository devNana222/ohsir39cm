package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.common.DatabaseCleanup;
import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.order.domain.OrderRepository;
import com.tdd.ecommerce.order.infrastructure.Order;
import com.tdd.ecommerce.order.infrastructure.OrderProduct;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@AutoConfigureMockMvc
@Sql(scripts = "/reset-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderIntegrationTest2 {
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

    private Order saveOrder(Long customerId) {
        Order order = new Order(null, customerId);
        return orderRepository.save(order);
    }

}
