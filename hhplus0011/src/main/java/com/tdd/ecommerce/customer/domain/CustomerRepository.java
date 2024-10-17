package com.tdd.ecommerce.customer.domain;

import com.tdd.ecommerce.customer.infrastructure.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Customer b SET b.balance = :newBalance WHERE b.customerId = :customerId")
    void updateBalance(@Param("customerId") Long customerId, @Param("newBalance") Long newBalance);

}
