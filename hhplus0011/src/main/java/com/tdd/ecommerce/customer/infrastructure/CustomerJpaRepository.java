package com.tdd.ecommerce.customer.infrastructure;

import com.tdd.ecommerce.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {
}
