package com.tdd.ecommerce.order.application;

import org.springframework.context.ApplicationEvent;

public class OrderEvent extends ApplicationEvent {
    private final Long orderId;

    public OrderEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
