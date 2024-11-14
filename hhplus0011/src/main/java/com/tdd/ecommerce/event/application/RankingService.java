package com.tdd.ecommerce.event.application;

import com.tdd.ecommerce.event.application.dto.RankingResponse;
import com.tdd.ecommerce.event.domain.Ranking;
import com.tdd.ecommerce.event.domain.RankingRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {
    private final RankingRepository rankingRepository;
    private final ProductRepository productRepository;

    public RankingService(RankingRepository rankingRepository, ProductRepository productRepository) {
        this.rankingRepository = rankingRepository;
        this.productRepository = productRepository;
    }

   @Cacheable( cacheNames = "getDaysRanking"
           , key = "#dateFormat.toLocalDate()"
           , unless = "#dateFormat == null")
   @CacheEvict(value = "dateFormat", key ="#dateFormat.toLocalDate().minusDays(3)")
    public List<RankingResponse> getThreeDaysRanking(LocalDateTime dateFormat){
        LocalDateTime threeDaysAgo = dateFormat.minusDays(2);
        Pageable pageable = PageRequest.of(0, 5);


        List<Ranking> orderedList = rankingRepository.findByNowdateForRanking(threeDaysAgo, pageable);

        return orderedList.stream()
                .map(r -> {
                    Long productId = r.getProductId();
                    Long orderCount = r.getOrderCount();

                    Product product = productRepository.findByProductId(productId);

                    return new RankingResponse(
                            product.getProductId(),
                            product.getProductName(),
                            orderCount,
                            product.getPrice(),
                            product.getCategory()
                    );
                }).collect(Collectors.toList());

    }
//key 가져오는 문법으로 테스트 > value가 있는지 없는지?
}
