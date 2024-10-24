package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.cart.domain.CartRepository;

import com.tdd.ecommerce.cart.domain.entity.Cart;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.domain.Customer;
import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.order.domain.OrderProductRepository;
import com.tdd.ecommerce.order.domain.OrderRepository;
import com.tdd.ecommerce.order.domain.Order;
import com.tdd.ecommerce.order.domain.OrderProduct;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class OrderService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final OrderRepository orderRepository;
    private final DataPlatformInterface dataPlatformInterface;
    private final OrderProductRepository orderProductRepository;
    private final CartRepository cartRepository;

    public List<OrderServiceResponse> getOrderList(Long orderId){
        Optional<Order> order = orderRepository.findById(orderId);

        if(order.isEmpty()){
            return Collections.emptyList();
        }

        List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);

        if(orderProducts.isEmpty()){
            log.warn("Order Product is Empty");
            return Collections.emptyList();
        }

        long totalPrice = 0L;

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
        if(dataPlatformInterface.sendOrderMessage(orders)) {

            return createOrderResponse(newOrder.getOrderId(), customerId, customer.getBalance(), orders);
        }
        else
            return Collections.emptyList();
    }

    @Transactional
    public List<OrderServiceResponse> createOrderFromCart(Long customerId, List<OrderProduct> orders){
        List<OrderServiceResponse> result = createOrder(customerId, orders);

        for(OrderProduct op : orders){
            updateCustomerCart(customerId, op.getProductId(), op.getAmount());
        }

        return result;
    }

    private boolean isEnoughBalance(Long customerId, Long requiredBalance){
        return customerRepository.findById(customerId).orElseThrow().getBalance() >= requiredBalance;
    }

    protected boolean isEnoughStock(Long productId, Long requiredStock) {
        return productInventoryRepository.findByProductIdWithLock(productId).getAmount() >= requiredStock;
    }

    private Customer getBalance(Long customerId) {
        return customerRepository.findById(customerId).get();
    }

    private Long getRequiredBalance(List<OrderProduct> orders){
        long totalRequiredBalance = 0L;

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
