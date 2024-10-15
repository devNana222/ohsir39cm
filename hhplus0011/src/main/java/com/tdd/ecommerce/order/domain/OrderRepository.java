package com.tdd.ecommerce.order.domain;

import com.tdd.ecommerce.order.infrastructure.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
