package com.tdd.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching //CacheManager가 빈에 등록되고 프로젝트 내에서 Cache를 처리할 수 있는 어노테이션을 사용할 수 있게 된다.
@EnableScheduling //@Scheduled의 동작을 위해 필요한 어노테이션
@EnableJpaAuditing
@SpringBootApplication
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
