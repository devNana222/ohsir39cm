package com.tdd.ecommerce.common.kafka;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.kafka-property")
public class KafkaCustomProperties {

    private String bootstrapServers;

    // Producer
    private String keySerializer;
    private String valueSerializer;

    // Consumer
    private String keyDeserializer;
    private String valueDeserializer;
    private String consumerGroupId;
}