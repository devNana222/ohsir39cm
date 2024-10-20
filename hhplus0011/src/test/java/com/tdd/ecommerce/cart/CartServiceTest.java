package com.tdd.ecommerce.cart;

import com.tdd.ecommerce.cart.application.CartService;
import com.tdd.ecommerce.cart.application.dto.CartRequest;
import com.tdd.ecommerce.cart.application.dto.CartResponse;
import com.tdd.ecommerce.cart.domain.CartRepository;
import com.tdd.ecommerce.cart.infrastructure.Cart;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.infrastructure.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    List<Cart> carts;

    @BeforeEach
    void setUp() {
        carts = new ArrayList<>();
        Cart cart = Cart.builder()
                .customerId(1L)
                .product(new Product(1L, "test", 100L, "etc", null))
                .amount(1L)
                .build();
        carts.add(cart);
    }

    @Test
    @DisplayName("🟢비어있는 장바구니 조회")
    void getCartProducts_SUCCESS_EMPTY() {
        Long customerId = 1L;

        when(cartRepository.findAllByCustomerId(customerId)).thenReturn(new ArrayList<>());

        List<Cart> result = cartRepository.findAllByCustomerId(customerId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("🟢정상적인 장바구니 조회")
    void getCartProduct_SUCCESS() {
        Long customerId = 1L;
        Product product = new Product(2L, "Product Name2", 100L, "etc", null);

        Cart cart = Cart.builder()
            .customerId(1L)
            .product(product)
            .amount(1L)
            .build();
        carts.add(cart);
        // 실제 서비스 메서드 호출
        List<CartResponse> result = cartService.getCartProducts(customerId);

        // 반환된 결과 검증
        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());
    }

    @Test
    @DisplayName("🔴이미 있는 상품")
    void addCartAlreadyExistsProduct() {
        Long customerId = 1L;
        Cart existingCart = Cart.builder()
                .customerId(1L)
                .product(new Product())
                .amount(3L).build();

        List<CartRequest> cartRequests = List.of(new CartRequest(1L, 3L));

        CartResponse result = cartService.addCartProducts(customerId, cartRequests);

        assertEquals(customerId, result.getCustomerId());
    }

    @Test
    void removeCart_SUCCESS() {
        Long customerId = 1L;
        when(cartRepository.findAllByCustomerId(customerId)).thenReturn(carts);

        cartService.removeCart(customerId);

        verify(cartRepository, times(1)).deleteByCustomerId(customerId);
    }

    @Test
    void removeCart_FAIL() {
        Long customerId = 1L;

        lenient().when(cartRepository.findAllByCustomerId(customerId)).thenReturn(Collections.emptyList());

        boolean result = cartService.removeCart(customerId);

        assertFalse(result);
        verify(cartRepository, never()).deleteByCustomerId(customerId);
    }
}
