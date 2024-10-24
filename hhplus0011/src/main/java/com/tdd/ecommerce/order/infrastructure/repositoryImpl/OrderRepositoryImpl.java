package com.tdd.ecommerce.order.infrastructure.repositoryImpl;

import com.tdd.ecommerce.order.domain.Order;
import com.tdd.ecommerce.order.domain.OrderRepository;
import com.tdd.ecommerce.order.infrastructure.jpaRepository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }
    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public void saveAll(List<Order> orders){
        orderJpaRepository.saveAll(orders);
    }
}
