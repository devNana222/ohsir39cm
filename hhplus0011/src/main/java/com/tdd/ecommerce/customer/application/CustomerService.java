package com.tdd.ecommerce.customer.application;

import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.domain.Customer;
import com.tdd.ecommerce.common.exception.BusinessException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerServiceResponse getCustomerBalance(Long customerId) {
        Optional<Customer> balance = Optional.ofNullable(customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ECommerceExceptions.INVALID_CUSTOMER)));

        return new CustomerServiceResponse(customerId, balance.get().getBalance());
    }

    public CustomerServiceResponse chargeCustomerBalance(Long customerId, Long chargeAmount) {
        Optional<Customer> customer = Optional.ofNullable(customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ECommerceExceptions.INVALID_CUSTOMER)));

        //Long newBalance = customer.get().chargeBalance(chargeAmount);
        customer.get().chargeBalance(chargeAmount);

        customerRepository.save(customer.get());

        return new CustomerServiceResponse(customerId, customer.get().getBalance());
    }

    @Transactional//connection 개수가 최대 10개
    public CustomerServiceResponse chargeCustomerBalanceWithOptimisticLocking(Long customerId, Long chargeAmount) {
        Long newBalance = 0L;

        try{
            Optional<Customer> customer = Optional.ofNullable(customerRepository.findByIdWithOptimisticLock(customerId)
                    .orElseThrow(() -> new BusinessException(ECommerceExceptions.INVALID_CUSTOMER)));
            customer.get().chargeBalance(chargeAmount);

            customerRepository.save(customer.get());
            newBalance = customer.get().getBalance();

        } catch(ObjectOptimisticLockingFailureException | OptimisticLockException e){
            log.info("Version 충돌");
            throw e;
        }
        return new CustomerServiceResponse(customerId, newBalance);
    }
}
