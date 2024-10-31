package com.tdd.ecommerce.customer.infrastructure;

import com.tdd.ecommerce.customer.domain.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT c FROM Customer c WHERE c.customerId = :customerId")
    Optional<Customer> findByIdWithOptimisticLock(Long customerId);
}
