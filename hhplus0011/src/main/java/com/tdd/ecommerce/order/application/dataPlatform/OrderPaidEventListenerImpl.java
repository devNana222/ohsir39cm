package com.tdd.ecommerce.order.application.dataPlatform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdd.ecommerce.common.domain.Outbox;
import com.tdd.ecommerce.common.domain.OutboxRepository;
import com.tdd.ecommerce.order.application.OrderEvent;
import com.tdd.ecommerce.order.presentation.dto.OrderProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidEventListenerImpl implements OrderPaidEventListenerInterface {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper om;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOrderOutbox(OrderEvent event){
        Outbox outbox = new Outbox(null, event.getOrderId(), "ORDER", "INIT");

        outboxRepository.save(outbox);
    }

    @Async
    @TransactionalEventListener(phase= TransactionPhase.AFTER_COMMIT)
    public void sendOrderMessage(OrderEvent event) throws JsonProcessingException {
        List<OrderProductRequest> order = event.getOrder();
        //Kafka에 메시지 전송
        kafkaTemplate.send("order-kafka", event.getOrderId().toString(), writeMessage(order));

    }

    private String writeMessage(List<OrderProductRequest> order) throws JsonProcessingException {
        return om.writeValueAsString(order);
    }
}