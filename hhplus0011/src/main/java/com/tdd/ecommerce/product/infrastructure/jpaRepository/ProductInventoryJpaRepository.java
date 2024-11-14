package com.tdd.ecommerce.product.infrastructure.jpaRepository;

import com.tdd.ecommerce.product.application.ProductServiceResponse;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductInventoryJpaRepository extends JpaRepository<ProductInventory, Long> {
    //재고가 있는 것 조회
    @Query("SELECT pi " +
            "FROM ProductInventory pi " +
            "WHERE pi.amount > 0")
    List<ProductInventory> findProductsByAmountGreaterThanZero();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pi " +
            "FROM ProductInventory pi " +
            "WHERE pi.productId = :productId")
    ProductInventory findByProductIdWithLock(@Param("productId") Long productId);

    @Query("SELECT new com.tdd.ecommerce.product.application.ProductServiceResponse(p.productId, p.productName, p.category, p.price, pi.amount) " +
            "FROM ProductInventory pi " +
            "INNER JOIN Product p " +
            "ON pi.productId = p.productId " +
            "WHERE pi.amount > 0")
    List<ProductServiceResponse> findProductsWithInventoryGreaterThanZero();
}
