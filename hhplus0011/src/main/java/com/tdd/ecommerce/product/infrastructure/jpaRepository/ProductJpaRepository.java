package com.tdd.ecommerce.product.infrastructure.jpaRepository;

import com.tdd.ecommerce.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    Product findByProductId(Long productId);
}
