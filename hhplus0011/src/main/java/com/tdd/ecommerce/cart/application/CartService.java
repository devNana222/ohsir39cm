package com.tdd.ecommerce.cart.application;

import com.tdd.ecommerce.cart.application.dto.CartDetailResponse;
import com.tdd.ecommerce.cart.application.dto.CartInfo;
import com.tdd.ecommerce.cart.application.dto.CartResult;
import com.tdd.ecommerce.cart.domain.CartRepository;
import com.tdd.ecommerce.cart.domain.entity.Cart;
import com.tdd.ecommerce.product.domain.ProductInfoDto;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;



@Service
@Slf4j
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    public List<CartResult> getCartProducts(Long customerId){

        List<Cart> cartList = getCartList(customerId).isEmpty() ? Collections.emptyList() : getCartList(customerId);

        List<CartResult> cartResponse = new ArrayList<>();
        List<CartDetailResponse> detailResponse = new ArrayList<>();

        for(Cart cart : cartList){
            Product product = cart.getProduct();
            ProductInfoDto pid = new ProductInfoDto(product.getProductId(), product.getProductName(), product.getPrice(), cart.getAmount());
            CartDetailResponse cartDetailResponse = new CartDetailResponse(pid, cart.getAmount());
            detailResponse.add(cartDetailResponse);
        }
        CartResult cartResult = new CartResult(customerId, detailResponse, LocalDateTime.now());
        cartResponse.add(cartResult);
        return cartResponse;
    }

    //Named Lock을 사용하는 경우 부모와 별도의 트랜잭션에서 수행되어야 하므로 Propagation 수준을 REQUIRES_NEW로 지정.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CartResult addCartProducts(Long customerId, List<CartInfo> cartInfos) {
        List<CartDetailResponse> responseProduct = new ArrayList<>();

        try{
            cartRepository.getLock(cartInfos.getFirst().productId().toString());

            List<Cart> carts = getCartList(customerId);
            log.info("최신 카트 : " + carts.getFirst().getProduct());

            for (CartInfo cr : cartInfos) {
                Optional<Cart> exists = carts.stream()
                        .filter(c -> c.getProduct().getProductId().equals(cr.productId()))
                        .findFirst();

                log.info("exists ? : " + exists.toString());
                if (exists.isPresent()) {

                    Cart existingCart = exists.get();
                    existingCart.addCartAmount(cr.amount());
                    log.info(existingCart.toString());
                    cartRepository.save(existingCart);
                    responseProduct.add(setProductDetail(cr.productId(), existingCart.getAmount()));
                } else {
                    // 카트에 물품이 존재하지 않을 경우 새롭게 추가
                    addProductsToCart(customerId, cr.productId(), cr.amount());
                    responseProduct.add(setProductDetail(cr.productId(), cr.amount()));
                }
            }
        }finally {
            cartRepository.releaseLock(cartInfos.getFirst().productId().toString());
        }

        return new CartResult(
                customerId,
                responseProduct,
                LocalDateTime.now()
        );
    }

    public boolean removeCart(Long customerId){
        boolean result = true;

        if(getCartList(customerId).isEmpty()){
            return false;
        }

        cartRepository.deleteByCustomerId(customerId);

        return result;
    }


    private List<Cart> getCartList(Long customerId){
        return cartRepository.findAllByCustomerId(customerId);
    }

    private CartDetailResponse setProductDetail(Long productId, Long productAmount){
        Product product = productRepository.findByProductId(productId);

        return new CartDetailResponse(
                new ProductInfoDto(product.getProductId(), product.getProductName(), product.getPrice(), productAmount),
                productAmount
        );
    }

    private void addProductsToCart(Long customerId, Long productId, Long amount){
        Cart cart = Cart.builder()
                .customerId(customerId)
                .product(productRepository.findByProductId(productId))
                .amount(amount)
                .build();

        cartRepository.save(cart);
    }

}
