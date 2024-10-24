package com.tdd.ecommerce.order.domain;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository {
    List<OrderProduct> findByOrderId(Long orderId);
    OrderProduct save(OrderProduct orderProduct);
    void saveAll(List<OrderProduct> orderProducts);
}
