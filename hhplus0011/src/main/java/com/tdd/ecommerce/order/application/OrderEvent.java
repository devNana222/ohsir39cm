package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.order.presentation.dto.OrderProductRequest;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class OrderEvent extends ApplicationEvent {
    private final List<OrderProductRequest> order;

    public OrderEvent(Object source, List<OrderProductRequest> order) {
        super(source);
        this.order = order;
    }

    public List<OrderProductRequest> getOrder() {
        return order;
    }
}
