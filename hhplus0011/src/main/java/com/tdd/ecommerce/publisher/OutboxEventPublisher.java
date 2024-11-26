package com.tdd.ecommerce.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdd.ecommerce.common.domain.Outbox;
import com.tdd.ecommerce.common.domain.OutboxRepository;
import com.tdd.ecommerce.order.domain.OrderOutboxStatus;
import com.tdd.ecommerce.order.domain.OrderProduct;
import com.tdd.ecommerce.order.domain.OrderProductRepository;
import com.tdd.ecommerce.order.presentation.dto.OrderProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {
    private final OutboxRepository outboxRepository;
    private final OrderProductRepository orderProductRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper om;

    @Scheduled(fixedDelay = 1000)
    public void publishInitializedEvents(){
        List<Outbox> initEvents = outboxRepository.findByStatus(OrderOutboxStatus.INIT.toString());
        log.info("outbox Scheduled start!");
        for(Outbox outbox : initEvents){
            try{
                List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(outbox.getEventId());
                List<OrderProductRequest> orderInfoList = new ArrayList<>();

                for (OrderProduct orderProduct : orderProducts) {
                    // OrderProductRequest 객체 생성 및 데이터 설정
                    OrderProductRequest orderProductRequest = new OrderProductRequest();
                    orderProductRequest.setProductId(orderProduct.getProductId());
                    orderProductRequest.setAmount(orderProduct.getAmount());

                    orderInfoList.add(orderProductRequest);
                }

                kafkaTemplate.send("order-kafka", outbox.getEventId().toString(), writeMessage(orderInfoList));
                outbox.setStatus(OrderOutboxStatus.PUBLISHED.toString());
                outboxRepository.save(outbox);
                log.info("Event published by Schedule");
            }catch(Exception e){
                log.error("Failed to publish event", e);
            }
        }
    }

    private String writeMessage(List<OrderProductRequest> order) throws JsonProcessingException {
        return om.writeValueAsString(order);
    }
}
