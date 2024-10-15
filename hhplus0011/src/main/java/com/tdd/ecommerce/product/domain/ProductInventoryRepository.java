package com.tdd.ecommerce.product.domain;

import com.tdd.ecommerce.product.infrastructure.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

    //재고가 있는 것 조회
    @Query("SELECT pi " +
            "FROM ProductInventory pi " +
            "WHERE pi.amount > 0")
    List<ProductInventory> findProductsByAmountGreaterThanZero();

}
