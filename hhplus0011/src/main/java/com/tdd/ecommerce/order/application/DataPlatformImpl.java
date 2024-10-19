package com.tdd.ecommerce.order.application;

import com.tdd.ecommerce.order.infrastructure.OrderProduct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataPlatformImpl implements DataPlatformInterface {

    public Boolean sendOrderMessage(List<OrderProduct> order) {
        System.out.println("주문 데이터가 전송되었습니다: " + order);
        return !order.isEmpty();
    }
}
