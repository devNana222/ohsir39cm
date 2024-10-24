package com.tdd.ecommerce.order.infrastructure.repositoryImpl;

import com.tdd.ecommerce.order.domain.OrderProduct;
import com.tdd.ecommerce.order.domain.OrderProductRepository;
import com.tdd.ecommerce.order.infrastructure.jpaRepository.OrderProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Repository
public class OrderProductRepositoryImpl implements OrderProductRepository {
    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public List<OrderProduct> findByOrderId(Long orderId){
        return orderProductJpaRepository.findByOrderId(orderId);
    }

    @Override
    public void saveAll(List<OrderProduct> orderProducts){
        orderProductJpaRepository.saveAll(orderProducts);
    }

    @Override
    public OrderProduct save(OrderProduct orderProduct){
        return orderProductJpaRepository.save(orderProduct);
    }
}
