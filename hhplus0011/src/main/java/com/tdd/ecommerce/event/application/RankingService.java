package com.tdd.ecommerce.event.application;

import com.tdd.ecommerce.event.application.dto.RankingResponse;
import com.tdd.ecommerce.event.domain.RankingRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.infrastructure.Product;
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

    public List<RankingResponse> getThreeDaysRanking(LocalDateTime dateFormat){
        LocalDateTime threeDaysAgo = dateFormat.minusDays(2);
        Pageable pageable = PageRequest.of(0, 5);


        List<Object[]> orderedList = rankingRepository.findByNowdateForRanking(threeDaysAgo, pageable);

        return orderedList.stream()
                .map(r -> {
                    Product product = productRepository.findByProductId((Long) r[0]);

                    return new RankingResponse(
                            product.getProductId(),
                            product.getProductName(),
                            (Long) r[1],
                            product.getPrice(),
                            product.getCategory()
                    );
                }).collect(Collectors.toList());

    }

}
