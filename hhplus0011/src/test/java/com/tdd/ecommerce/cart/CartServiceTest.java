package com.tdd.ecommerce.cart;

import com.tdd.ecommerce.cart.domain.CartService;
import com.tdd.ecommerce.cart.application.dto.CartInfo;
import com.tdd.ecommerce.cart.application.dto.CartResult;
import com.tdd.ecommerce.cart.domain.CartRepository;
import com.tdd.ecommerce.cart.domain.entity.Cart;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductInventoryRepository productInventoryRepository;

    @InjectMocks
    private CartService cartService;

    List<Cart> carts;

    private List<Cart> existingCarts;

    Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Test Product", 1000L, "etc", null);
        Cart existingCart = new Cart(1L, 1L, 3L, product);  // 초기 수량 3개
        existingCarts = List.of(existingCart);
    }

    @Test
    @DisplayName("🟢비어있는 장바구니를 조회하면 빈 값이 반환된다.")
    void getCartProducts_SUCCESS_EMPTY() {
        Long customerId = 1L;

        when(cartRepository.findAllByCustomerId(customerId)).thenReturn(new ArrayList<>());

        List<Cart> result = cartRepository.findAllByCustomerId(customerId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("🟢상품 하나를 장바구니에 넣고 장바구니 번호를 조회하면 하나의 상품이 나온다.")
    void getCartProduct_SUCCESS() {
        Long customerId = 1L;
        Product product = new Product(2L, "Product Name2", 100L, "etc", null);

        Cart cart = Cart.builder()
            .customerId(1L)
            .product(product)
            .amount(1L)
            .build();
        carts.add(cart);

        List<CartResult> result = cartService.getCartProducts(customerId);

        // 반환된 결과 검증
        assertEquals(1, result.size());
        assertEquals(customerId, result.getFirst().getCustomerId());
    }

    @Test
    @DisplayName("🟢이미 있는 상품을 장바구니에 넣으면 합산 개수가 반환된다.")
    void addCartAlreadyExistsProduct() {
        // given
        Long customerId = 1L;
        Long productId = 1L;
        Long addAmount = 2L;

        CartInfo cartInfo = new CartInfo(productId, addAmount);
        List<CartInfo> cartInfos = List.of(cartInfo);

        // when
        when(cartRepository.findAllByCustomerId(customerId)).thenReturn(existingCarts);  // 장바구니에 상품이 존재
        when(productRepository.findByProductId(productId)).thenReturn(product);

        // 실행
        CartResult cartResult = cartService.addCartProducts(customerId, cartInfos);

        // then
        // 기존 장바구니 상품의 수량이 3개에서 2개 추가되어 5개가 되는지 확인
        assertEquals(5L, cartResult.getProductInfoDtoList().getFirst().getProductAmount());

    }

    @Test
    @DisplayName("🟢장바구니를 전체 비우면 true가 반환된다.")
    void removeCart_success() {
        // given
        Long customerId = 1L;
        List<Cart> cartList = List.of(new Cart());

        // when
        when(cartRepository.findAllByCustomerId(customerId)).thenReturn(cartList);
        boolean result = cartService.removeCart(customerId);

        // then
        assertTrue(result);
        verify(cartRepository, times(1)).deleteByCustomerId(customerId);
    }

    @Test
    @DisplayName("🔴존재하지 않는 장바구니를 삭제하면 실행되지않는다.")
    void removeCart_failure() {
        // given
        Long customerId = 2L;

        // when
        when(cartRepository.findAllByCustomerId(customerId)).thenReturn(List.of());
        boolean result = cartService.removeCart(customerId);

        // then
        assertFalse(result);
        verify(cartRepository, never()).deleteByCustomerId(customerId);
    }
}
