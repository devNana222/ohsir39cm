package com.tdd.ecommerce.customer.presentation;


import com.tdd.ecommerce.customer.application.CustomerService;
import com.tdd.ecommerce.customer.application.CustomerServiceResponse;
import com.tdd.ecommerce.customer.presentation.dto.ChargeRequest;
import com.tdd.ecommerce.customer.presentation.dto.BalanceResponse;
import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.common.model.CommonApiResponse;
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
public class BalanceController {

    private final CustomerService customerService;

    @Autowired
    public BalanceController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{customer_id}/balance")
    public ResponseEntity<CommonApiResponse<?>> getBalance(@PathVariable("customer_id") Long customerId) {
        try {
            CustomerServiceResponse customerServiceResponse = customerService.getCustomerBalance(customerId);

            BalanceResponse result = new BalanceResponse(customerId, customerServiceResponse.getBalance());

            CommonApiResponse<BalanceResponse> response = new CommonApiResponse<>(true, "조회를 성공했습니다. 현재 고객님의 잔액입니다.", result);

            return ResponseEntity.ok().body(response);

        } catch (BusinessException e) {
            // 고객이 존재하지 않을 경우 오류 처리
            CommonApiResponse<ECommerceExceptions> errorResponse = new CommonApiResponse<>(false, e.getMessage(), ECommerceExceptions.INVALID_CUSTOMER);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


  @PatchMapping("/{customer_id}/balance/charge")
  public ResponseEntity<CommonApiResponse<?>> chargeBalance(@PathVariable("customer_id") Long customerId, @RequestBody ChargeRequest request) {
      try {
          request.validate();

          CustomerServiceResponse customerServiceResponse = customerService.chargeCustomerBalance(customerId, request.balance());

          CommonApiResponse<Long> response = new CommonApiResponse<>(true, "충전이 완료되었습니다.", customerServiceResponse.getBalance());

          return ResponseEntity.ok(response);

      } catch (IllegalArgumentException e) {
          CommonApiResponse<String> errorResponse = new CommonApiResponse<>(false, e.getMessage(), "INVALID_CHARGE_AMOUNT");

          return ResponseEntity.badRequest().body(errorResponse);
      } catch (BusinessException e) {
          CommonApiResponse<String> errorResponse = new CommonApiResponse<>(false, e.getMessage(), "INVALID_CUSTOMER");

          return ResponseEntity.badRequest().body(errorResponse);
      }
  }
}
