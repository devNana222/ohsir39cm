package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductInventoryRepository productInventoryRepository) {
        this.productRepository = productRepository;
        this.productInventoryRepository = productInventoryRepository;
    }

    public List<ProductServiceResponse> getProductByProductId(Long productId) {
        Product product = productRepository.findByProductId(productId);

        if(product == null)
            throw new BusinessException(ECommerceExceptions.INVALID_PRODUCT);

        Optional<ProductInventory> productInventory = productInventoryRepository.findById(productId);

        if (productInventory.isEmpty() || productInventory.get().getAmount() <= 0) {
            throw new BusinessException(ECommerceExceptions.OUT_OF_STOCK);
        }

        ProductServiceResponse response = new ProductServiceResponse(
                product.getProductId(),
                product.getProductName(),
                product.getCategory(),
                product.getPrice(),
                productInventory.get().getAmount()
        );

        return Collections.singletonList(response);
    }

    public List<ProductServiceResponse> getProducts(){
        List<ProductInventory> productInventories = productInventoryRepository.findProductsByAmountGreaterThanZero();

        if (productInventories == null) {
            throw new BusinessException(ECommerceExceptions.OUT_OF_STOCK);
        }

        return productInventories.stream()
                .map(inventory -> {
                    Product product = productRepository.findByProductId(inventory.getProductId());

                    return new ProductServiceResponse(
                            product.getProductId(),
                            product.getProductName(),
                            product.getCategory(),
                            product.getPrice(),
                            inventory.getAmount()
                    );
                }).collect(Collectors.toList());
    }

}
