package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.infrastructure.Customer;
import com.tdd.ecommerce.common.exception.BusinessException;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductInventoryRepository productInventoryRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DataPlatformInterface dataPlatformInterface;

    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private OrderService orderService;

    Product product1;
    Product product2;

    @BeforeEach
    public void setUp() {
        product1 = new Product(1L,"테스트 1", 30000L, "etc", null);
        product2 = new Product(2L,"테스트 2", 20000L, "etc", null);
    }

    @Test
    @DisplayName("🟢정상적인 주문결과 조회")
    void getOrderList_SUCCESS() {
        //given
        Long orderId = 1L;
        Order order = new Order(orderId, 1L);
        OrderProduct orderProduct1 = new OrderProduct(1L, orderId, 1L, 3L, 30000L);
        OrderProduct orderProduct2 = new OrderProduct(2L, orderId, 2L, 2L, 20000L);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderProductRepository.findByOrderId(orderId)).thenReturn(List.of(orderProduct1, orderProduct2));
        when(productRepository.findByProductId(1L)).thenReturn(product1);
        when(productRepository.findByProductId(2L)).thenReturn(product2);

        //when
        List<OrderServiceResponse> orderInfo = orderService.getOrderList(orderId);

        //then
        assertEquals(130000L, orderInfo.get(0).getBalance());

    }
    @Test
    @DisplayName("🔴주문이 존재하지 않는 경우")
    void getOrderList_ORDER_NOT_FOUND() {
        // given
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when
        List<OrderServiceResponse> orderInfo = orderService.getOrderList(orderId);

        // then
        assertTrue(orderInfo.isEmpty());
    }

    @Test
    @DisplayName("🔴주문 상품이 존재하지 않는 경우")
    void getOrderList_NO_ORDER_PRODUCTS() {
        // given
        Long orderId = 1L;
        Order order = new Order(orderId, 1L); // customerId: 1L

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderProductRepository.findByOrderId(orderId)).thenReturn(Collections.emptyList());

        // when
        List<OrderServiceResponse> orderInfo = orderService.getOrderList(orderId);

        // then
        assertTrue(orderInfo.isEmpty());
    }

    @Test
    void createOrder_SUCCESS() {
        //given
        Long orderId = 1L;
        Long customerId = 1L;

        List<OrderProduct> orderProducts = List.of(new OrderProduct(null, 1L, 1L, 2L, 30000L));

        Customer customer = new Customer(customerId, 5000000L);
        ProductInventory inventory = new ProductInventory(1L, 1L, 10L);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findByProductId(1L)).thenReturn(product1);
        when(productInventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order(1L, customerId));
        when(dataPlatformInterface.sendOrderMessage(anyList())).thenReturn(true);

        //when
        List<OrderServiceResponse> response = orderService.createOrder(orderId, orderProducts);

        //then
        assertFalse(response.isEmpty());
        verify(orderProductRepository, times(1)).saveAll(anyList());
        verify(dataPlatformInterface, times(1)).sendOrderMessage(anyList());
    }

    @Test
    @DisplayName("🔴재고 부족")
    void createOrder_OUT_OF_STOCK() {
        // given
        List<OrderProduct> orderProducts = List.of(
                new OrderProduct(null, 1L, 1L, 2L, 30000L)
        );

        ProductInventory inventory = new ProductInventory(1L, 1L, 1L);

        when(productInventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        //when & then
        assertThrows(BusinessException.class, () -> orderService.createOrder(1L, orderProducts));

    }

    @Test
    @DisplayName("🔴잔액 부족")
    void createOrder_INSUFFICIENT_BALANCE() {
        // given
        Long customerId = 1L;
        List<OrderProduct> orderProducts = List.of(new OrderProduct(null, 1L, 1L, 2L, 10000L));

        Customer customer = new Customer(null, 10000L);  // 잔액 부족
        Product product = new Product(1L, "Test Product", 10000L, "etc", null);
        ProductInventory inventory = new ProductInventory(1L, 10L,5L);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findByProductId(1L)).thenReturn(product);
        when(productInventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        // when & then
        assertThrows(BusinessException.class, () -> orderService.createOrder(customerId, orderProducts));

    }
}