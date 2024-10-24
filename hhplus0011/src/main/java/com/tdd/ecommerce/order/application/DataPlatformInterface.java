package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.order.domain.OrderProduct;

import java.util.List;

public interface DataPlatformInterface {
    Boolean sendOrderMessage(List<OrderProduct> order);
}
