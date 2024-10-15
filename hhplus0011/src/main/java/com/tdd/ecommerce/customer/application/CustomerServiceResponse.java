package com.tdd.ecommerce.customer.application;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerServiceResponse {
    private Long customerId;
    private Long balance;
}
