package com.tdd.ecommerce.common.config.kafka;

import com.tdd.ecommerce.common.domain.Outbox;
import com.tdd.ecommerce.common.domain.OutboxRepository;
import com.tdd.ecommerce.order.domain.OrderOutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OutboxRepository outboxRepository;

    @KafkaListener(topics = "#{@environment.getProperty('application.topic.topic_order')}")
    public void sendOrderInfoConsumer(ConsumerRecord<String, String> consumerRecord) {
        String key = consumerRecord.key();
        System.out.println("key = " + key);
        String value = consumerRecord.value();
        System.out.println("value = " + value);

        Outbox outbox = outboxRepository.findByEventId(Long.parseLong(key));

        if (outbox == null) {
            log.warn("outbox is null");
        }
        else{
            log.info("outbox = " + outbox);
            outbox.setStatus(OrderOutboxStatus.PUBLISHED.toString());
            outboxRepository.save(outbox);
        }
    }
    //새로운 아웃박스를 만든다?

}