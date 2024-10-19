package com.tdd.ecommerce.cart;

import com.tdd.ecommerce.cart.application.CartService;
import com.tdd.ecommerce.cart.application.dto.CartRequest;
import com.tdd.ecommerce.cart.application.dto.CartResponse;
import com.tdd.ecommerce.cart.domain.CartRepository;
import com.tdd.ecommerce.cart.infrastructure.Cart;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.infrastructure.Customer;
import com.tdd.ecommerce.product.application.ProductService;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.infrastructure.Product;
import com.tdd.ecommerce.product.infrastructure.ProductInventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CartIntegrationTest {

    @Autowired
    private CartService sut;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("🟢고객의 카트상품들 가져오기")
    void getCustomerCart_SUCCESS() {
        Long customerId = createNewCustomerAndGetId();

        setProductInCart(customerId, 1L, 10L);
        setProductInCart(customerId, 2L, 20L);

        List<CartResponse> result = sut.getCartProducts(customerId);

        assertThat(result.get(0).getProductInfoDtoList().get(0).getProductAmount()).isEqualTo(10L);
        assertThat(result.get(0).getProductInfoDtoList().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("🟢고객이 카트에 담아놓은 상품이 없을 때")
    void getCustomerCart_NO_PRODUCT() {
        Long customerId = createNewCustomerAndGetId();

        List<CartResponse> result = sut.getCartProducts(customerId);

        assertThat(result.get(0).getProductInfoDtoList()).isEmpty();
    }

    @Test
    @DisplayName("🟢카트에 상품 추가(새로운 상품)")
    void addCartNewProduct_SUCCESS() {
        Long customerId = 1L;
        Long productId = insertNewProductAndReturnProductId();
        CartRequest cartRequest = new CartRequest(productId, 1L);
        List<CartRequest> cartRequests = Collections.singletonList(cartRequest);

        List<CartResponse> result = Stream.of(sut.addCartProducts(customerId, cartRequests)).toList();

        assertThat(result.get(0).getProductInfoDtoList()).hasSize(1);
        assertThat(result.get(0).getProductInfoDtoList().get(0).getProductAmount()).isEqualTo(1L);

    }

    @Test
    @DisplayName("🟢카트에 상품 추가(기존에 있는 상품)")
    void addCartExistsProduct_SUCCESS() {
        Long customerId = createNewCustomerAndGetId();

        Long pi = insertNewProductAndReturnProductId();
        setProductInCart(customerId, pi, 1L);

        CartRequest cartRequest = new CartRequest(pi, 1L);
        List<CartRequest> cartRequests = Collections.singletonList(cartRequest);

        Long productId = cartRequests.getFirst().productId();
        Long amount = cartRequests.getFirst().amount();

        CartRequest cartRequest2 = new CartRequest(productId, 2L);

        List<CartRequest> cartRequests2 = Collections.singletonList(cartRequest2);

        List<CartResponse> result = Stream.of(sut.addCartProducts(customerId, cartRequests2)).toList();

        assertThat(result.get(0).getProductInfoDtoList().get(0).getProductAmount()).isEqualTo(amount+2L);

    }

    @Test
    @DisplayName("🟢카트 삭제")
    void removeCart_SUCCESS(){
        Long customerId = 1L;

        setProductInCart(customerId, 1L, 10L);

        boolean result = sut.removeCart(customerId);

        assertThat(result).isTrue();

        Optional<Cart> cart = getCartProduct(customerId);
        assertThat(cart).isEmpty();
    }

    @Test
    @DisplayName("🔴삭제 실패")
    void removeCart_FAIL(){
        Long customerId = 1L;

        setProductInCart(customerId, 1L, 10L);
        sut.removeCart(customerId);
        boolean result = sut.removeCart(customerId);

        assertThat(result).isFalse();
    }

    private Long createNewCustomerAndGetId(){
        Customer customer = new Customer(null, 10000L);

        return customerRepository.save(customer).getCustomerId();
    }

    private void setProductInCart(Long customerId, Long productId, Long amount){
        Cart cart = new Cart();
        cart.setCustomerId(customerId);
        cart.setProduct(productRepository.findByProductId(productId));
        cart.setAmount(amount);

        cartRepository.save(cart);
    }

    private Long insertNewProductAndReturnProductId(){
        Product product = new Product(null, "테스트상품", 1000L, "etc",null);
        Long productId = productRepository.save(product).getProductId();

        ProductInventory inventory = new ProductInventory(null, productId, 10L);
        productInventoryRepository.save(inventory);

        return productId;
    }

    private Optional<Cart> getCartProduct(Long customerId){
        return cartRepository.findAllByCustomerId(customerId).stream().findFirst();
    }
}
