package com.tdd.ecommerce.cart.domain;

import com.tdd.ecommerce.cart.domain.entity.Cart;

import java.util.List;

public interface CartRepository {

    Cart save(Cart cart);

    List<Cart> findAllByCustomerId(Long customerId);

    void deleteByCustomerId(Long customerId);

    void deleteCartByCustomerIdAndProductId(Long customerId, Long productId);
}
