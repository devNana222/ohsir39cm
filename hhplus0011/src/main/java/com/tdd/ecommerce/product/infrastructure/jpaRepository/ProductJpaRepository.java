package com.tdd.ecommerce.product.infrastructure.jpaRepository;

import com.tdd.ecommerce.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p " +
            "FROM Product p " +
            "WHERE p.productId = :productId")
    Product findByProductId(Long productId);
}
