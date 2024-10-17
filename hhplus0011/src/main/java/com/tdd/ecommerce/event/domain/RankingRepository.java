package com.tdd.ecommerce.event.domain;

import com.tdd.ecommerce.order.infrastructure.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface RankingRepository extends JpaRepository<OrderProduct, Long> {

    @Query("SELECT op.productId, SUM(op.amount) AS totalSales " +
            "FROM OrderProduct op " +
            "WHERE op.regDate >= :threeDaysAgo " +
            "GROUP BY op.productId " +
            "ORDER BY totalSales DESC ")
    List<Object[]> findByNowdateForRanking(@Param("threeDaysAgo") LocalDateTime threeDaysAgo, Pageable pageable);
}
