package com.tdd.ecommerce.customer.infrastructure;

import com.tdd.ecommerce.customer.domain.Customer;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Repository
public class CustomerRepositoryImpl  implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;

    @Override
    public Optional<Customer> findById(Long customerId) {
        return customerJpaRepository.findById(customerId);
    }

    @Override
    public Customer save(Customer customer) {
        return customerJpaRepository.save(customer);
    }
}
