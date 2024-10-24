package com.tdd.ecommerce.cart.application;

import com.tdd.ecommerce.cart.application.dto.CartDetailResponse;
import com.tdd.ecommerce.cart.application.dto.CartRequest;
import com.tdd.ecommerce.cart.application.dto.CartResponse;
import com.tdd.ecommerce.cart.domain.CartRepository;
import com.tdd.ecommerce.cart.infrastructure.Cart;
import com.tdd.ecommerce.product.domain.ProductInfoDto;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.infrastructure.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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


    public List<CartResponse> getCartProducts(Long customerId){

        List<Cart> cartList = getCartList(customerId).isEmpty() ? Collections.emptyList() : getCartList(customerId);

        List<CartResponse> cartResponses = new ArrayList<>();
        List<CartDetailResponse> detailResponse = new ArrayList<>();

        for(Cart cart : cartList){
            Product product = cart.getProduct();
            ProductInfoDto pid = new ProductInfoDto(product.getProductId(), product.getProductName(), product.getPrice(), cart.getAmount());
            CartDetailResponse cartDetailResponse = new CartDetailResponse(pid, cart.getAmount());
            detailResponse.add(cartDetailResponse);
        }
        CartResponse cartResponse = new CartResponse(customerId, detailResponse, LocalDateTime.now());
        cartResponses.add(cartResponse);
        return cartResponses;
    }

    public CartResponse addCartProducts(Long customerId, List<CartRequest> cartRequests) {

        List<Cart> carts = getCartList(customerId);

        List<CartDetailResponse> responseProduct = new ArrayList<>();

        for (CartRequest cr : cartRequests) {
            if (carts.isEmpty()) {

                addProductsToCart(customerId, cr.productId(), cr.amount());
                responseProduct.add(setProductDetail(cr.productId(), cr.amount()));
            }
            else{
                Optional<Cart> exists = carts.stream()
                        .filter(c -> c.getProduct().getProductId().equals(cr.productId()))
                        .findFirst();

                exists.ifPresentOrElse(
                        cart -> cart.addCartAmount(cr.amount()),
                        () -> addProductsToCart(customerId, cr.productId(), cr.amount())
                );

                responseProduct.add(setProductDetail(cr.productId(), exists.get().getAmount()));
            }

        }
        return new CartResponse(
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
