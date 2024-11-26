package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.order.presentation.dto.OrderProductRequest;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class OrderEvent extends ApplicationEvent {
    private final List<OrderProductRequest> order;
    private final Long orderId;

    public OrderEvent(Object source, Long orderId, List<OrderProductRequest> order) {
        super(source);
        this.orderId = orderId;
        this.order = order;
    }

    public List<OrderProductRequest> getOrder() {
        return order;
    }
    public Long getOrderId() {return orderId;}
}
