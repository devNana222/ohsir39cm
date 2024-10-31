package com.tdd.ecommerce.customer.presentation;

import com.tdd.ecommerce.customer.application.CustomerService;
import com.tdd.ecommerce.customer.domain.Customer;
import com.tdd.ecommerce.customer.domain.CustomerRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
@Sql(scripts = "/reset-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CustomerConcurrencyTest {

    @Autowired
    CustomerService sut;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @DisplayName("한 고객에게 복수의 포인트 충전 요청을 보내면 한번만 성공한다.")
    void chargeMultiPoint() throws InterruptedException {

        int requestNum = 2;
        Long chargeAmount = 2000L;

        Long customerId = customerRepository.save(new Customer(null, 1000L, 0L)).getCustomerId();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(requestNum);

        ExecutorService executorService = Executors.newFixedThreadPool(requestNum);

        for(int i = 0; i < requestNum; i++){
            executorService.submit(()->{
               try{
                   sut.chargeCustomerBalanceWithOptimisticLocking(customerId, chargeAmount);

                   successCount.incrementAndGet();
               }catch(OptimisticLockException | ObjectOptimisticLockingFailureException e){
                   failureCount.incrementAndGet();
               }finally {
                   latch.countDown();
               }
            });
        }
        latch.await();
        executorService.shutdown();

        // 잔액 및 성공, 실패 카운트 확인
        Long finalBalance = sut.getCustomerBalance(customerId).getBalance();
        System.out.println("최종 잔액: " + finalBalance);
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failureCount.get());

        // Thread의 DB Connection은 최대 10개이므로, 10개이상 동시에 요청을 했을 때에는 1개 이상의 요청이 성공할 수 있다.
        assertEquals(1, successCount.get());
        assertEquals(chargeAmount * successCount.get() + 1000L , finalBalance); // 초기 잔액 1000L
    }
}
