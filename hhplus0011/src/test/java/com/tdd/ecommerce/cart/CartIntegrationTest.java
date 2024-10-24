package com.tdd.ecommerce.cart;

import com.tdd.ecommerce.cart.domain.CartService;
import com.tdd.ecommerce.cart.application.dto.CartInfo;
import com.tdd.ecommerce.cart.application.dto.CartResult;
import com.tdd.ecommerce.cart.domain.CartRepository;
import com.tdd.ecommerce.cart.domain.entity.Cart;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.domain.Customer;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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


    @BeforeEach
    public void setUp() {
        Product product = new Product(1L, "Test Product", 10000L,"etc", null);
        Product product2 = new Product(2L, "Test Product2", 1000L,"etc", null);
        Long productId = productRepository.save(product).getProductId();
        Long productId2 = productRepository.save(product2).getProductId();

        productInventoryRepository.save(new ProductInventory(null, productId, 30L));
        productInventoryRepository.save(new ProductInventory(null, productId2, 30L));
    }

    @Test
    @DisplayName("🟢고객의 카트상품들 가져오기")
    void getCustomerCart_SUCCESS() {
        Long customerId = createNewCustomerAndGetId();

        setProductInCart(customerId, 1L, 10L);
        setProductInCart(customerId, 2L, 20L);

        List<CartResult> result = sut.getCartProducts(customerId);

        assertThat(result.get(0).getProductInfoDtoList().get(0).getProductAmount()).isEqualTo(10L);
        assertThat(result.get(0).getProductInfoDtoList().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("🟢고객이 카트에 담아놓은 상품이 없을 때")
    void getCustomerCart_NO_PRODUCT() {
        Long customerId = createNewCustomerAndGetId();

        List<CartResult> result = sut.getCartProducts(customerId);

        assertThat(result.get(0).getProductInfoDtoList()).isEmpty();
    }

    @Test
    @DisplayName("🟢카트에 상품 추가(새로운 상품)")
    void addCartNewProduct_SUCCESS() {
        Long customerId = 1L;
        Long productId = insertNewProductAndReturnProductId();
        CartInfo cartInfo = new CartInfo(productId, 1L);
        List<CartInfo> cartInfos = Collections.singletonList(cartInfo);

        List<CartResult> result = Stream.of(sut.addCartProducts(customerId, cartInfos)).toList();

        assertThat(result.get(0).getProductInfoDtoList()).hasSize(1);
        assertThat(result.get(0).getProductInfoDtoList().get(0).getProductAmount()).isEqualTo(1L);

    }

    @Test
    @DisplayName("🟢카트에 상품 추가(기존에 있는 상품)")
    void addCartExistsProduct_SUCCESS() {
        Long customerId = createNewCustomerAndGetId();

        Long pi = insertNewProductAndReturnProductId();
        setProductInCart(customerId, pi, 1L);

        CartInfo cartInfo = new CartInfo(pi, 1L);
        List<CartInfo> cartInfos = Collections.singletonList(cartInfo);

        Long productId = cartInfos.getFirst().productId();
        Long amount = cartInfos.getFirst().amount();

        CartInfo cartInfo2 = new CartInfo(productId, 2L);

        List<CartInfo> cartRequests2 = Collections.singletonList(cartInfo2);

        List<CartResult> result = Stream.of(sut.addCartProducts(customerId, cartRequests2)).toList();

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
        Cart cart = Cart.builder()
                .customerId(customerId)
                .product(productRepository.findByProductId(productId))
                .amount(amount)
                .build();
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
