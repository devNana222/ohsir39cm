package com.tdd.ecommerce.order.application.dataPlatform;

import com.tdd.ecommerce.order.domain.OrderProduct;

import java.util.List;

public interface DataPlatformInterface {
    Boolean sendOrderMessage(List<OrderProduct> order);
}
