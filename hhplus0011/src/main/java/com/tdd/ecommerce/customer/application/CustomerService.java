package com.tdd.ecommerce.customer.application;

import com.tdd.ecommerce.customer.domain.CustomerRepository;
import com.tdd.ecommerce.customer.infrastructure.Customer;
import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
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
        Optional<Customer> balance = customerRepository.findById(customerId);

        if (balance.isEmpty()) {
            throw new BusinessException(ECommerceExceptions.INVALID_CUSTOMER);
        }
        return new CustomerServiceResponse(customerId, balance.get().getBalance());
    }

    public CustomerServiceResponse chargeCustomerBalance(Long customerId, Long chargeAmount) {
        Optional<Customer> balance = customerRepository.findById(customerId);

        if (balance.isEmpty()) {
            throw new BusinessException(ECommerceExceptions.INVALID_CUSTOMER);
        }

        Long newBalance = balance.get().chargeBalance(chargeAmount);

        // 변경된 엔티티를 저장합니다.
        customerRepository.save(balance.get());

        return new CustomerServiceResponse(customerId, newBalance);
    }
}
