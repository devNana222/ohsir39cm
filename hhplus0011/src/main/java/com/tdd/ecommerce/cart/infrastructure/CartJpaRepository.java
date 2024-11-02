package com.tdd.ecommerce.cart.infrastructure;

import com.tdd.ecommerce.cart.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartJpaRepository extends JpaRepository<Cart, Long> {

    @Override
    Cart save(Cart cart);

    @Query("SELECT c FROM Cart c WHERE c.customerId = :customerId")
    List<Cart> findAllByCustomerId(Long customerId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.customerId = :customerId")
    void deleteByCustomerId(Long customerId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.customerId = :customerId AND c.product.productId = :productId")
    void deleteCartByCustomerIdAndProductId(Long customerId, Long productId);

    @Query(value = "select get_lock(:key, 100)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
