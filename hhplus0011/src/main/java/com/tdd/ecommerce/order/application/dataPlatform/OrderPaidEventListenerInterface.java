package com.tdd.ecommerce.order.application.dataPlatform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tdd.ecommerce.order.application.OrderEvent;

public interface OrderPaidEventListenerInterface {
    void sendOrderMessage(OrderEvent event) throws JsonProcessingException;
}
