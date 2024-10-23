package com.tdd.ecommerce.event.presentation;

import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.common.model.ResponseUtil;
import com.tdd.ecommerce.event.application.RankingService;
import com.tdd.ecommerce.event.application.dto.RankingResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Tag(
        name = "랭킹 시스템",
        description = "조회 날짜 기준으로 최근 3일간 가장 많이 팔린 TOP 5 상품을 보여줍니다."
)
@RestController
@RequestMapping("/ranks")
public class RankingController {
    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/{today}")
    public ResponseEntity<?> getTopRank(@PathVariable("today") String today) {
        LocalDateTime dateFormat;
        try{
            dateFormat = LocalDate.parse(today).atStartOfDay();
        } catch (DateTimeParseException e) {

           return ResponseUtil.buildErrorResponse(ECommerceExceptions.INVALID_DATE, "잘못된 날짜 형식입니다.");
        }

        List<RankingResponse> ranking = rankingService.getThreeDaysRanking(dateFormat);

        return ResponseUtil.buildSuccessResponse(today + " 기준 판매 순위 TOP 5입니다.", ranking);
    }

}
