package com.tdd.ecommerce.order.infrastructure.jpaRepository;

import com.tdd.ecommerce.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Override
    Order save(Order order);

}
