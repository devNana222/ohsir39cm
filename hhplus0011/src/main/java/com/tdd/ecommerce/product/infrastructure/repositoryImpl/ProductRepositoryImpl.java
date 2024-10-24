package com.tdd.ecommerce.product.infrastructure.repositoryImpl;

import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.infrastructure.jpaRepository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product findByProductId(Long productId){
        return productJpaRepository.findByProductId(productId);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public void saveAll(Iterable<Product> products) {
        productJpaRepository.saveAll(products);
    }
}
