package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.cart.domain.CartRepository;
import com.tdd.ecommerce.cart.domain.entity.Cart;
import com.tdd.ecommerce.common.domain.OutboxRepository;
import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.customer.domain.Customer;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.order.domain.Order;
import com.tdd.ecommerce.order.domain.OrderProduct;
import com.tdd.ecommerce.order.domain.OrderProductRepository;
import com.tdd.ecommerce.order.domain.OrderRepository;
import com.tdd.ecommerce.order.presentation.dto.OrderProductRequest;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final OrderProductRepository orderProductRepository;
    private final CartRepository cartRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OutboxRepository  outboxRepository;

    public List<OrderServiceResponse> getOrderList(Long orderId){
        Optional<Order> order = orderRepository.findById(orderId);

        if(order.isEmpty()){
            return Collections.emptyList();
        }

        List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);
        List<OrderInfo> orderInfoList = new ArrayList<>();

        for (OrderProduct orderProduct : orderProducts) {
            // OrderProductRequest 객체 생성 및 데이터 설정
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setProductId(orderProduct.getProductId());
            orderInfo.setAmount(orderProduct.getAmount());

            orderInfoList.add(orderInfo);
        }

        if(orderProducts.isEmpty()){
            log.warn("Order Product is Empty");
            return Collections.emptyList();
        }

        long totalPrice = 0L;

        for(OrderProduct orderProduct : orderProducts){
            totalPrice += orderProduct.getPrice() * orderProduct.getAmount();
        }

        return createOrderResponse(orderId, order.get().getCustomerId(), totalPrice, orderInfoList);
    }

    @Transactional
    public List<OrderServiceResponse> createOrder(Long customerId, List<OrderProductRequest> orders) {
        Customer customer;
        Order newOrder;


        List<OrderInfo> orderInfoList = new ArrayList<>();
        try {
            Long totalRequiredBalance = getRequiredBalance(orders);

            customer = getBalance(customerId);

            customer.checkSufficientBalance(totalRequiredBalance);

            newOrder = saveOrder(customerId);

            customer.useBalance(totalRequiredBalance);

            customerRepository.save(customer);
            saveOrderProduct(newOrder.getOrderId(), orders);
            updateProductInventory(orders);


            for (OrderProductRequest orderProduct : orders) {
                // OrderProductRequest 객체 생성 및 데이터 설정
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setProductId(orderProduct.getProductId());
                orderInfo.setAmount(orderProduct.getAmount());

                orderInfoList.add(orderInfo);
            }
            //outbox table 에 이벤트 저장한 다음에 이벤트 발행해야함.

            eventPublisher.publishEvent(new OrderEvent(this, newOrder.getOrderId(), orders));
        }
        catch(Exception e){
            log.warn("Order creation failed", e);
            throw new RuntimeException("Order creation failed", e);
        }
        return createOrderResponse(newOrder.getOrderId(), customerId, customer.getBalance(), orderInfoList);
    }

    @Transactional
    public List<OrderServiceResponse> createOrderFromCart(Long customerId, List<OrderProductRequest> orders){
        List<OrderServiceResponse> result = createOrder(customerId, orders);

        for(OrderProductRequest op : orders){
            updateCustomerCart(customerId, op.getProductId(), op.getAmount());
        }

        return result;
    }

    protected boolean isEnoughStock(Long productId, Long requiredStock) {
        return productInventoryRepository.findByProductIdWithLock(productId).getAmount() >= requiredStock;
    }

    private Customer getBalance(Long customerId) {
        return customerRepository.findById(customerId).get();
    }

    private Long getRequiredBalance(List<OrderProductRequest> orders){
        long totalRequiredBalance = 0L;

        for(OrderProductRequest order : orders){
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

    private void updateProductInventory(List<OrderProductRequest> orders) {
        for (OrderProductRequest order : orders) {
            Long productId = order.getProductId();
            Long requiredStock = order.getAmount();

            ProductInventory inventory = productInventoryRepository.findById(productId).orElseThrow(() ->
                    new BusinessException(ECommerceExceptions.INVALID_PRODUCT));

            inventory.decreaseAmount(requiredStock);
            productInventoryRepository.save(inventory);
        }
    }
    private void saveOrderProduct(Long orderId, List<OrderProductRequest> orderProducts) {
        List<OrderProduct> orderInfos = new ArrayList<>();

        for (OrderProductRequest orderProduct : orderProducts) {
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

    private List<OrderServiceResponse> createOrderResponse(Long orderId, Long customerId, Long points, List<OrderInfo> orders) {
        List<OrderServiceResponse.OrderProductInfo> orderProductInfos = orders.stream()
                .map(order -> {
                    Product product = productRepository.findByProductId(order.getProductId());
                    Long price = product.getPrice()* order.getAmount();
                    return new OrderServiceResponse.OrderProductInfo(order.getProductId(), product.getProductName(), order.getAmount(), price);
                })
                .collect(Collectors.toList());

        return Collections.singletonList(new OrderServiceResponse(orderId, customerId, points, orderProductInfos));
    }

    private void updateCustomerCart(Long customerId, Long productId, Long amount){
        Optional<Cart> cart = cartRepository.findAllByCustomerId(customerId).stream()
                .filter(c -> c.getProduct().getProductId().equals(productId))
                .findFirst();

        if (cart.isPresent()) {
            Cart existingCart = cart.get();

            existingCart.changeAmount(amount);

            if (existingCart.getAmount() <= 0L) {
                cartRepository.deleteCartByCustomerIdAndProductId(customerId, productId);
            } else {
                cartRepository.save(existingCart);
            }
        }
    }
}
