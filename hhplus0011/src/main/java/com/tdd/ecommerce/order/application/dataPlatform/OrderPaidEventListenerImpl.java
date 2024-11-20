package com.tdd.ecommerce.order.application.dataPlatform;

import com.tdd.ecommerce.common.domain.Outbox;
import com.tdd.ecommerce.common.domain.OutboxRepository;
import com.tdd.ecommerce.order.application.OrderEvent;
import com.tdd.ecommerce.order.domain.OrderOutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidEventListenerImpl implements OrderPaidEventListenerInterface {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;

    @Async
    @TransactionalEventListener(phase= TransactionPhase.AFTER_COMMIT)
    public void sendOrderMessage(OrderEvent event) {
        Long orderId = event.getOrderId();
        try{
            //Kafka에 메시지 전송
            kafkaTemplate.send("order-kafka", "ORDER", orderId.toString());
        }catch(Exception e){
            log.error(e.getMessage());
            Outbox outbox = outboxRepository.findByEventId(orderId);

            outbox.setStatus(OrderOutboxStatus.FAILED.toString());
            outboxRepository.save(outbox);
        }
    }

}