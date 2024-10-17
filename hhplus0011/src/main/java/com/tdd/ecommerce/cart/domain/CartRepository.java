package com.tdd.ecommerce.cart.domain;

import com.tdd.ecommerce.cart.infrastructure.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart save(Cart cart);

    @Query("SELECT c FROM Cart c WHERE c.cartId = :cartId")
    List<Cart> findByCartId(Long cartId);

    @Query("SELECT c FROM Cart c WHERE c.customerId = :customerId")
    List<Cart> findAllByCustomerId(Long customerId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.customerId = :customerId")
    void deleteByCustomerId(Long customerId);
}
