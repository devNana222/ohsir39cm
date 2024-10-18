package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.infrastructure.Customer;
import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.order.domain.OrderProductRepository;
import com.tdd.ecommerce.order.domain.OrderRepository;
import com.tdd.ecommerce.order.infrastructure.Order;
import com.tdd.ecommerce.order.infrastructure.OrderProduct;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.infrastructure.Product;
import com.tdd.ecommerce.product.infrastructure.ProductInventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class OrderService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final OrderRepository orderRepository;
    private final FakePGService fakePGService;
    private final OrderProductRepository orderProductRepository;

    public List<OrderServiceResponse> getOrderList(Long orderId){
        Optional<Order> order = orderRepository.findById(orderId);

        if(order.isEmpty()){
            return Collections.emptyList();
        }

        List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);

        if(orderProducts.isEmpty()){
            return Collections.emptyList();
        }

        Long totalPrice = 0L;

        for(OrderProduct orderProduct : orderProducts){
            totalPrice += orderProduct.getPrice() * orderProduct.getAmount();
        }

        return createOrderResponse(orderId, order.get().getCustomerId(), totalPrice, orderProducts);
    }

    @Transactional
    public List<OrderServiceResponse> createOrder(Long customerId, List<OrderProduct> orders) {
        Long totalRequiredBalance = getRequiredBalance(orders);

        Customer customer = getBalance(customerId);

        if(!isEnoughBalance(customerId, totalRequiredBalance)) {
            throw new BusinessException(ECommerceExceptions.INSUFFICIENCY_BALANCE);
        }

        Order newOrder = saveOrder(customerId);

        customer.useBalance(totalRequiredBalance);

        customerRepository.save(customer);
        saveOrderProduct(newOrder.getOrderId(), orders);
        updateProductInventory(orders);

        //결제 성공 시 주문 정보 데이터 플랫폼에 전송
        if(fakePGService.sendOrderToDataPlatform(orders)) {

            List<OrderServiceResponse> response = createOrderResponse(newOrder.getOrderId(), customerId, customer.getBalance(), orders);

            return response;
        }
        else
            return Collections.emptyList();
    }

    private boolean isEnoughBalance(Long customerId, Long requiredBalance){
        return customerRepository.findById(customerId).orElseThrow().getBalance() >= requiredBalance;
    }

    private boolean isEnoughStock(Long productId, Long requiredStock) {
        return productInventoryRepository.findByProductIdWithLock(productId).getAmount() >= requiredStock;
    }

    private Customer getBalance(Long customerId) {
        return customerRepository.findById(customerId).get();
    }

    private Long getRequiredBalance(List<OrderProduct> orders){
        Long totalRequiredBalance = 0L;

        for(OrderProduct order : orders){
            Long productId = order.getProductId();
            Long requiredStock = order.getAmount();

            if(!isEnoughStock(productId, requiredStock)) {
                throw new BusinessException(ECommerceExceptions.OUT_OF_STOCK);
            }

            Product product = productRepository.findByProductId(productId);
            totalRequiredBalance += product.getPrice() * requiredStock;
        }

        return totalRequiredBalance;
    }

    private Order saveOrder(Long customerId) {
        Order order = new Order(null, customerId);
        return orderRepository.save(order);
    }

    private void updateProductInventory(List<OrderProduct> orders) {
        for (OrderProduct order : orders) {
            Long productId = order.getProductId();
            Long requiredStock = order.getAmount();

            ProductInventory inventory = productInventoryRepository.findById(productId).orElseThrow(() ->
                    new BusinessException(ECommerceExceptions.INVALID_PRODUCT));

            inventory.decreaseAmount(requiredStock);
            productInventoryRepository.save(inventory);
        }
    }
    private void saveOrderProduct(Long orderId, List<OrderProduct> orderProducts) {
        List<OrderProduct> orderInfos = new ArrayList<>();

        for (OrderProduct orderProduct : orderProducts) {
            Product product = productRepository.findByProductId(orderProduct.getProductId());

            Long price = product.getPrice();

            OrderProduct newOrderProduct = new OrderProduct(
                    null,
                    orderId,
                    orderProduct.getProductId(),
                    orderProduct.getAmount(),
                    price
            );

            orderInfos.add(newOrderProduct);
        }

        orderProductRepository.saveAll(orderInfos);
    }

    private List<OrderServiceResponse> createOrderResponse(Long orderId, Long customerId, Long points, List<OrderProduct> orders) {
        List<OrderServiceResponse.OrderProductInfo> orderProductInfos = orders.stream()
                .map(order -> {
                    Product product = productRepository.findByProductId(order.getProductId());
                    Long price = product.getPrice()* order.getAmount();
                    return new OrderServiceResponse.OrderProductInfo(order.getProductId(), product.getProductName(), order.getAmount(), price);
                })
                .collect(Collectors.toList());

        return Collections.singletonList(new OrderServiceResponse(orderId, customerId, points, orderProductInfos));
    }
}
