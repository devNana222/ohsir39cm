package com.tdd.ecommerce.customer.presentation;


import com.tdd.ecommerce.common.exception.CommonExceptions;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.common.model.ResponseUtil;
import com.tdd.ecommerce.customer.application.CustomerService;
import com.tdd.ecommerce.customer.application.CustomerServiceResponse;
import com.tdd.ecommerce.customer.presentation.dto.CustomerRequest;
import com.tdd.ecommerce.customer.presentation.dto.CustomerResponse;
import com.tdd.ecommerce.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/customers")
@RestController
@Tag(
        name = "고객 - 고객 포인트 시스템",
        description = "고객 잔여포인트 조회/충전 API"
)
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{customer_id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable("customer_id") Long customerId) {
        try {
            CustomerServiceResponse customerServiceResponse = customerService.getCustomerBalance(customerId);

            CustomerResponse result = new CustomerResponse(customerId, customerServiceResponse.getBalance());

            return ResponseUtil.buildSuccessResponse("고객번호 : "+ customerId + "님의 잔액 정보입니다.", result);
        } catch (BusinessException e) {
            // 고객이 존재하지 않을 경우 오류 처리
            return ResponseUtil.buildErrorResponse(ECommerceExceptions.INVALID_CUSTOMER, ECommerceExceptions.INVALID_CUSTOMER.getMessage());
        }
    }


  @PatchMapping("/{customer_id}/balance/charge")
  public ResponseEntity<?> chargeBalance(@PathVariable("customer_id") Long customerId, @RequestBody CustomerRequest request) {
      try {
          request.validate();

          CustomerServiceResponse customerServiceResponse = customerService.chargeCustomerBalance(customerId, request.balance());
          return ResponseUtil.buildSuccessResponse(request.balance() + " point 충전이 완료되었습니다..", customerServiceResponse);

      } catch (IllegalArgumentException e) {
          return ResponseUtil.buildErrorResponse(CommonExceptions.INVALID_PARAMETER, CommonExceptions.INVALID_PARAMETER.getMessage());
      } catch (BusinessException e) {
          return ResponseUtil.buildErrorResponse(ECommerceExceptions.INVALID_CUSTOMER, ECommerceExceptions.INVALID_CUSTOMER.getMessage());
      }
  }

    @PatchMapping("/{customer_id}/balance/charge2")
    public ResponseEntity<?> chargeBalanceWithOptimisticLock(@PathVariable("customer_id") Long customerId, @RequestBody CustomerRequest request) {
        try {
            request.validate();

            CustomerServiceResponse customerServiceResponse = customerService.chargeCustomerBalanceWithOptimisticLocking(customerId, request.balance());
            return ResponseUtil.buildSuccessResponse(request.balance() + " point 충전이 완료되었습니다..", customerServiceResponse);

        } catch (IllegalArgumentException e) {
            return ResponseUtil.buildErrorResponse(CommonExceptions.INVALID_PARAMETER, CommonExceptions.INVALID_PARAMETER.getMessage());
        }
    }
}
