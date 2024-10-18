package com.tdd.ecommerce.order.domain;

import com.tdd.ecommerce.order.infrastructure.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderId(Long orderId);

    @Query("SELECT count(op.productId) FROM OrderProduct op WHERE op.productId = :productId")
    Long countByProductId(@Param("productId") Long productId);

    @Query("DELETE FROM OrderProduct op WHERE op.productId = :productId")
    Long deleteByProductId(@Param("productId") Long productId);
}
