package com.tdd.ecommerce.product.domain;

import com.tdd.ecommerce.product.domain.entity.Product;

public interface ProductRepository {

    Product findByProductId(Long productId);
    Product save(Product product);
    void saveAll(Iterable<Product> products);
}
