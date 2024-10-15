package com.tdd.ecommerce.product.domain;

import com.tdd.ecommerce.product.infrastructure.Product;
import com.tdd.ecommerce.product.infrastructure.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByProductId(Long productId);

}
