package com.tdd.ecommerce.common.infrastructure.event;

import com.tdd.ecommerce.common.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findByStatus(String status);
    Outbox findByEventId(Long eventId);
}
