package com.tdd.ecommerce.event.application;

import com.tdd.ecommerce.event.application.dto.RankingResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class RankingIntegrationTest {

    @Autowired
    RankingService sut;

    @Test
    @DisplayName("🟢 정상적인 판매 순위 조회")
    void getThreeDaysRanking_INTEGRATION_TEST() throws Exception {
        // given
        LocalDateTime date = LocalDateTime.parse("2024-10-17T00:00:00");
        LocalDateTime threeDaysAgo = date.minusDays(2);

        //when
        List<RankingResponse> result = sut.getThreeDaysRanking(threeDaysAgo);

        //then
        assertThat(result.get(0).salesCount() > result.get(1).salesCount());
    }
}
