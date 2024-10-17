package com.tdd.ecommerce.event.application;

import com.tdd.ecommerce.event.application.dto.RankingResponse;
import com.tdd.ecommerce.event.domain.RankingRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.infrastructure.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @InjectMocks
    RankingService rankingService;

    @Mock
    ProductRepository productRepository;
    @Mock
    RankingRepository rankingRepository;

    @Test
    @DisplayName("🟢정상적으로 TOP 5 를 가져온다.")
    void getThreeDaysRanking_SUCCESS() {
        LocalDateTime dateFormat = LocalDateTime.now();
        LocalDateTime threeDaysAgo = dateFormat.minusDays(2);

        Pageable pageable = PageRequest.of(0,5);

        List<Object[]> orderList = new ArrayList<>();
        orderList.add(new Object[]{1L, 200L});
        orderList.add(new Object[]{2L, 300L});

        when(rankingRepository.findByNowdateForRanking(threeDaysAgo, pageable)).thenReturn(orderList);

        Product fakeProduct1 = new Product(1L, "Product A", 500L, "etc", null);
        Product fakeProduct2 = new Product(2L, "Product B", 800L, "etc", null);

        when(productRepository.findByProductId(1L)).thenReturn(fakeProduct1);
        when(productRepository.findByProductId(2L)).thenReturn(fakeProduct2);

        List<RankingResponse> result = rankingService.getThreeDaysRanking(dateFormat);
        assertEquals(2, result.size());
    }
}