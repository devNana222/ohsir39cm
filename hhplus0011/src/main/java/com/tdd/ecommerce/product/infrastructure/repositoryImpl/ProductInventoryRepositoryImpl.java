package com.tdd.ecommerce.product.infrastructure.repositoryImpl;

import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.infrastructure.jpaRepository.ProductInventoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Repository
public class ProductInventoryRepositoryImpl implements ProductInventoryRepository {

    private final ProductInventoryJpaRepository productInventoryJpaRepository;

    @Override
    public List<ProductInventory> findProductsByAmountGreaterThanZero(){
        return productInventoryJpaRepository.findProductsByAmountGreaterThanZero();
    }

    @Override
    public ProductInventory findByProductIdWithLock(Long productId){
        return  productInventoryJpaRepository.findByProductIdWithLock(productId);
    }

    @Override
    public void updateStock( Long productId, Long newAmount){
        productInventoryJpaRepository.updateStock(productId, newAmount);
    }

    @Override
    public ProductInventory save(ProductInventory productInventory){
        return productInventoryJpaRepository.save(productInventory);
    }

    @Override
    public Optional<ProductInventory> findById(Long productId){
        return productInventoryJpaRepository.findById(productId);
    }

}
