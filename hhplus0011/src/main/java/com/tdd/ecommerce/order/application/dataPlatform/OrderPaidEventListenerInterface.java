package com.tdd.ecommerce.order.application.dataPlatform;

import com.tdd.ecommerce.order.application.OrderEvent;
import com.tdd.ecommerce.order.domain.OrderProduct;

import java.util.List;

public interface OrderPaidEventListenerInterface {
    void sendOrderMessage(OrderEvent event);
}
