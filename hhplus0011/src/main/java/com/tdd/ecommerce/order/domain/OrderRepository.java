package com.tdd.ecommerce.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long orderId);
    Order save(Order order);
    void saveAll(List<Order> orders);
}
