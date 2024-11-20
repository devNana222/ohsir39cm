package com.tdd.ecommerce.common.infrastructure.event;

import com.tdd.ecommerce.common.domain.Outbox;
import com.tdd.ecommerce.common.domain.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Repository
public class OutboxRepositoryImpl implements OutboxRepository {
    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox outbox){
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public List<Outbox> findByStatus(String status){
        return outboxJpaRepository.findByStatus(status);
    }

    @Override
    public Outbox findByEventId(Long eventId){
        return outboxJpaRepository.findByEventId(eventId);
    }
}
