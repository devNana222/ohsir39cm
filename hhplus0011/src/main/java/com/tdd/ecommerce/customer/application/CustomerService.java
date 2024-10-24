package com.tdd.ecommerce.customer.application;

import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.infrastructure.Customer;
import com.tdd.ecommerce.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        Long newBalance = customer.get().chargeBalance(chargeAmount);

        // 변경된 엔티티를 저장합니다.
        customerRepository.save(customer.get());

        return new CustomerServiceResponse(customerId, newBalance);
    }
}
