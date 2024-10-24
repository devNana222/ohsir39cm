package com.tdd.ecommerce.order.domain;

import java.util.List;

public interface OrderProductRepository {
    List<OrderProduct> findByOrderId(Long orderId);
    OrderProduct save(OrderProduct orderProduct);
    void saveAll(List<OrderProduct> orderProducts);
}
