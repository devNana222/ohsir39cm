package com.tdd.ecommerce.customer.presentation;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.customer.application.CustomerService;
import com.tdd.ecommerce.customer.application.CustomerServiceResponse;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
public class CustomerIntegrationTest {

    @Autowired
    CustomerService sut;

    @Test
    @DisplayName("🟢고객 1명의 포인트 가져오기")
    void getCustomerBalance_SUCCESS() {
        //given
        Long customerId = 4L;
        Long balance = 0L;
        //when
        CustomerServiceResponse result = sut.getCustomerBalance(customerId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getBalance()).isEqualTo(balance);

    }

    @Test
    @DisplayName("🔴없는 고객의 포인트 조회")
    void getCustomerBalance_INVALID_CUSTOMER() {
        //given
        Long customerId = 40L;
        Long balance = 0L;

        //when & then
        assertThatThrownBy(() -> sut.getCustomerBalance(customerId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ECommerceExceptions.INVALID_CUSTOMER.getMessage());

    }

    @Test
    @DisplayName("🟢고객 포인트 충전")
    void chargeBalance_SUCCESS(){
        Long customerId = 4L;
        Long chargeAmount = 100000L;

        CustomerServiceResponse result = sut.chargeCustomerBalance(customerId, chargeAmount);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getBalance()).isEqualTo(chargeAmount);

    }

    @Test
    @DisplayName("🔴없는 고객에 대한 충전")
    void chargeBalance_INVALID_USER(){
        Long customerId = 400L;
        Long chargeAmount = 5L;

        //when&then
        assertThatThrownBy(() -> sut.chargeCustomerBalance(customerId, chargeAmount))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ECommerceExceptions.INVALID_CUSTOMER.getMessage());

    }
    //예외 객체의 경우, 동일한 예외 클래스와 메시지가 발생하더라도 객체 비교는 서로 다른 인스턴스로 취급됨.
}
