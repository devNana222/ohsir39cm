package com.tdd.ecommerce.event.infrastructure;

import com.tdd.ecommerce.event.domain.Ranking;
import com.tdd.ecommerce.order.domain.OrderProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RankingJpaRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT new com.tdd.ecommerce.event.domain.Ranking(op.productId, SUM(op.amount)) " +
            "FROM OrderProduct op " +
            "WHERE op.regDate >= :threeDaysAgo " +
            "GROUP BY op.productId " +
            "ORDER BY SUM(op.amount) DESC ")
    List<Ranking> findByNowdateForRanking(@Param("threeDaysAgo") LocalDateTime threeDaysAgo, Pageable pageable);
}
