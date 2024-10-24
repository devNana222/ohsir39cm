package com.tdd.ecommerce.customer.domain;

import com.tdd.ecommerce.customer.infrastructure.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
