package com.tdd.event.publisher;

import com.tdd.ecommerce.common.domain.Outbox;
import com.tdd.ecommerce.common.domain.OutboxRepository;
import com.tdd.ecommerce.order.domain.OrderOutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 5000)
    public void publishInitializedEvents(){
        List<Outbox> initEvents = outboxRepository.findByStatus(OrderOutboxStatus.INIT.toString());

        for(Outbox outbox : initEvents){
            try{
                kafkaTemplate.send("order-kafka", "ORDER", outbox.getEventId().toString());
                outbox.setStatus(OrderOutboxStatus.PUBLISHED.toString());
                outboxRepository.save(outbox);
                log.info("Event published by Schedule");
            }catch(Exception e){
                log.error("Failed to publish event", e);
            }
        }
    }
}
