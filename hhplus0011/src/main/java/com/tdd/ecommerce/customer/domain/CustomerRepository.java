package com.tdd.ecommerce.customer.domain;

import java.util.Optional;

public interface CustomerRepository{

    Optional<Customer> findById(Long customerId);
    Customer save(Customer customer);
}
