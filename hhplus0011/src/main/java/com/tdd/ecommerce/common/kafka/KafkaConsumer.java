package com.tdd.ecommerce.common.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    @KafkaListener(topics = "#{@environment.getProperty('application.topic.topic_order')}")
    public void consumer1(ConsumerRecord<String, String> consumerRecord) {
        String key = consumerRecord.key();
        System.out.println("key = " + key);
        String value = consumerRecord.value();
        System.out.println("value = " + value);
    }
}