package com.tdd.ecommerce.event.domain;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface RankingRepository{

    List<Ranking> findByNowdateForRanking(LocalDateTime threeDaysAgo, Pageable pageable);
}
