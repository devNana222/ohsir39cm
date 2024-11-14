package com.tdd.ecommerce.product.domain;

import com.tdd.ecommerce.product.application.ProductServiceResponse;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;

import java.util.List;
import java.util.Optional;

public interface ProductInventoryRepository {

   List<ProductInventory> findProductsByAmountGreaterThanZero();

    ProductInventory findByProductIdWithLock(Long procuctId);

    ProductInventory save(ProductInventory productInventory);

    Optional<ProductInventory> findById(Long productId);

    List<ProductServiceResponse> findProductsWithInventoryGreaterThanZero();
}
