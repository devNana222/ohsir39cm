package com.tdd.ecommerce.product.domain;

import com.tdd.ecommerce.product.infrastructure.ProductInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pi " +
            "FROM ProductInventory pi " +
            "WHERE pi.productId = :procuctId")
    ProductInventory findByProductIdWithLock(@Param("procuctId") Long procuctId);

    @Modifying
    @Query("UPDATE ProductInventory pi SET pi.amount = :newAmount WHERE pi.productId = :productId")
    void updateStock(@Param("productId") Long productId, @Param("newAmount") Long newAmount);

}
