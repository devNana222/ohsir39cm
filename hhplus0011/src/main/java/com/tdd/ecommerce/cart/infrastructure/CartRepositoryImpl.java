package com.tdd.ecommerce.cart.infrastructure;

import com.tdd.ecommerce.cart.domain.entity.Cart;
import com.tdd.ecommerce.cart.domain.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Repository
public class CartRepositoryImpl implements CartRepository {

    private final CartJpaRepository cartJpaRepository;

    @Override
    public Cart save(Cart cart){
        return cartJpaRepository.save(cart);
    }

    @Override
    public List<Cart> findAllByCustomerId(Long customerId){
        return cartJpaRepository.findAllByCustomerId(customerId);
    }

    @Override
    public void deleteByCustomerId(Long customerId){
        cartJpaRepository.deleteByCustomerId(customerId);
    }

    @Override
    public void deleteCartByCustomerIdAndProductId(Long customerId, Long productId){
        cartJpaRepository.deleteCartByCustomerIdAndProductId(customerId, productId);
    }

    @Override
    public void getLock(String key){
        cartJpaRepository.getLock(key);
    }

    @Override
    public void releaseLock(String key){
        cartJpaRepository.releaseLock(key);
    }
}
