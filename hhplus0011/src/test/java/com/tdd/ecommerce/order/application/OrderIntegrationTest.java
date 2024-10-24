package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.domain.Customer;
import com.tdd.ecommerce.order.domain.OrderProductRepository;
import com.tdd.ecommerce.order.domain.OrderRepository;
import com.tdd.ecommerce.order.domain.Order;
import com.tdd.ecommerce.order.domain.OrderProduct;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
    @Autowired
    private OrderProductRepository orderProductRepository;

    @BeforeEach
    public void setUp() {

        Customer customer = new Customer(null, 100000L);
        customerId = customerRepository.save(customer).getCustomerId();

        Product product = new Product(null, "Test Product", 10000L,"etc", null);
        productId = productRepository.save(product).getProductId();

        ProductInventory inventory = new ProductInventory(inventoryId, productId, 30L);

        inventoryId = productInventoryRepository.save(inventory).getId();
    }

    @Test
    @Rollback
    @DisplayName("🟢1L상품의 주문을 성공하면 반환되는 리스트의 상품코드는 1L, 주문수량은 1L이다.")
    void createOrder_SUCCESS(){
        Long orderId = saveOrder(customerId).getOrderId();
        OrderProduct orderProduct = new OrderProduct(null, orderId, productId, 1L, 10000L);
        List<OrderProduct> orders = Collections.singletonList(orderProduct);

        List<OrderServiceResponse> response = sut.createOrder(customerId, orders);

        // 응답 검증
        assertThat(response).isNotEmpty();
        assertThat(response.getFirst().getOrderProducts().getFirst().getProductId()).isEqualTo(productId);
        assertThat(response.getFirst().getOrderProducts().getFirst().getAmount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("🟢주문번호1L의 주문 정보는 비어있지 않고 상품코드가 1L이다.")
    void getOrderList_SUCCESS(){
        Long orderId = saveOrder(customerId).getOrderId();
        OrderProduct orderProduct = new OrderProduct(null, orderId, productId, 1L, 10000L);
        orderProductRepository.save(orderProduct);

        List<OrderServiceResponse> response = sut.getOrderList(orderId);
        assertThat(response).isNotEmpty();
        assertThat(response.getFirst().getOrderProducts().getFirst().getProductId()).isEqualTo(productId);
    }


    private Order saveOrder(Long customerId) {
        Order order = new Order(null, customerId);
        return orderRepository.save(order);
    }
}
