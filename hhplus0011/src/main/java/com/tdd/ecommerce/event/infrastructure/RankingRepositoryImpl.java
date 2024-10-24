package com.tdd.ecommerce.event.infrastructure;

import com.tdd.ecommerce.event.domain.Ranking;
import com.tdd.ecommerce.event.domain.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Repository
public class RankingRepositoryImpl implements RankingRepository {
    private final RankingJpaRepository rankingJpaRepository;

    @Override
    public List<Ranking> findByNowdateForRanking(LocalDateTime threeDaysAgo, Pageable pageable){
        return rankingJpaRepository.findByNowdateForRanking(threeDaysAgo, pageable);
    }

}
