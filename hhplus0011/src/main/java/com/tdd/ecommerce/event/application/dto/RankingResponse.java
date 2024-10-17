package com.tdd.ecommerce.event.application.dto;

public record RankingResponse(
        Long productId,
        String productName,
        Long salesCount,
        Long price,
        String category
) {
}
