package com.tdd.ecommerce.event.domain;

import lombok.Getter;

@Getter
public final class Ranking {
    private final Long productId;
    private final Long orderCount;

    public Ranking(Long productId, Long orderCount) {
        this.productId = productId;
        this.orderCount = orderCount;
    }
}
