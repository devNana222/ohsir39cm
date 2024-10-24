package com.tdd.ecommerce.order.infrastructure.jpaRepository;

import com.tdd.ecommerce.order.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductJpaRepository extends JpaRepository<OrderProduct,Long> {

    List<OrderProduct> findByOrderId(Long orderId);
}
