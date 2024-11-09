package com.tdd.ecommerce.event.application;

import com.tdd.ecommerce.event.application.dto.RankingResponse;
import com.tdd.ecommerce.event.domain.RankingRepository;
import com.tdd.ecommerce.order.domain.OrderProductRepository;
import com.tdd.ecommerce.order.domain.OrderRepository;
import com.tdd.ecommerce.order.domain.Order;
import com.tdd.ecommerce.order.domain.OrderProduct;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RankingIntegrationTest {

    @Autowired
    RankingService sut;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderProductRepository orderProductRepository;
    @Autowired
    ProductRepository productRepository;

    @SpyBean
    RankingRepository spyRankingRepository;

    @Autowired
    private RankingService rankingService;


    @BeforeEach
    void setUp() {
        List<Product> products = List.of(
                new Product(1L, "test1",1000L, "옷", null),
                new Product(2L, "test2", 300L, "양말", null),
                new Product(3L, "test3", 1300L, "모자", null)
        );
        productRepository.saveAll(products);

        List<Order> orders = List.of(
                new Order(1L, 3L),
                new Order(2L, 4L),
                new Order(3L, 3L),
                new Order(4L, 4L)
        );

        orderRepository.saveAll(orders);
        List<OrderProduct> orderProducts = List.of(
                new OrderProduct(null, 1L, 1L, 30L, 1000L),
                new OrderProduct(null, 1L, 3L, 20L, 10000L ),
                new OrderProduct(null, 2L, 1L, 10L, 1000L),
                new OrderProduct(null, 3L, 2L, 10L, 1000L),
                new OrderProduct(null, 4L, 1L, 10L, 1000L)
        );
        orderProductRepository.saveAll(orderProducts);
    }

    @Test
    @DisplayName("🟢첫번째 반환된 상품의 판매 개수는 두번째 반환된 상품의 판매 개수보다 많다.")
    void getThreeDaysRanking_INTEGRATION_TEST() throws Exception {
        // given
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime threeDaysAgo = date.minusDays(2);

        //when
        List<RankingResponse> result = sut.getThreeDaysRanking(threeDaysAgo);

        //then
        assertThat(result.get(0).salesCount() > result.get(1).salesCount());
    }

    @Test
    @DisplayName("🟢Cache 잘 적용되었는지 확인하기.")
    void validateRankingCache(){
        LocalDateTime date = LocalDateTime.now();

        //캐시 적용 전
        rankingService.getThreeDaysRanking(date);

        verify(spyRankingRepository, times(1)).findByNowdateForRanking(any(),any(Pageable.class));

        //캐시 적용 후
        rankingService.getThreeDaysRanking(date);

        verify(spyRankingRepository, times(1)).findByNowdateForRanking(any(),any(Pageable.class));

        rankingService.getThreeDaysRanking(date.minusDays(1)); //다른날짜로 했을 때 조회되는지 확인
        verify(spyRankingRepository, times(2)).findByNowdateForRanking(any(),any(Pageable.class));

    }
}
