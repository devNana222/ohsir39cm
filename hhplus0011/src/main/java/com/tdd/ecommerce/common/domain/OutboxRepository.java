package com.tdd.ecommerce.common.domain;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxRepository {
    Outbox save(Outbox outbox);

    @Query(
            "SELECT o " +
            "From Outbox o " +
            "WHERE o.status = :status "
    )
    List<Outbox> findByStatus(String status);

    @Query(
            "SELECT o " +
            "From Outbox o " +
            "WHERE o.eventId = :eventId "
    )
    Outbox findByEventId(Long eventId);
}