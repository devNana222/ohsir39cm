package com.tdd.ecommerce.order.application.dataPlatform;

import com.tdd.ecommerce.order.application.OrderEvent;
import com.tdd.ecommerce.order.domain.OrderProduct;
import com.tdd.ecommerce.order.presentation.dto.OrderProductRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
public class OrderPaidEventListenerImpl implements OrderPaidEventListenerInterface {

    @Async
    @TransactionalEventListener(phase= TransactionPhase.AFTER_COMMIT)
    public void sendOrderMessage(OrderEvent event) {
        List<OrderProductRequest> order = event.getOrder();
        log.info("주문 데이터가 전송되었습니다: " + order);
    }
}
