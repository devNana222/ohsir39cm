package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import com.tdd.ecommerce.product.domain.ProductInfoDto;
import com.tdd.ecommerce.product.domain.ProductInventoryRepository;
import com.tdd.ecommerce.product.domain.ProductRepository;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductInventoryRepository productInventoryRepository) {
        this.productRepository = productRepository;
        this.productInventoryRepository = productInventoryRepository;
    }

    @Cacheable(cacheNames = "getAllProductInfo"
            , key = "'products'")
    public List<ProductServiceResponse> getProducts() {
        List<ProductServiceResponse> products = productInventoryRepository.findProductsWithInventoryGreaterThanZero();

        if (products.isEmpty()) {
            throw new BusinessException(ECommerceExceptions.OUT_OF_STOCK);
        }

        return products;
    }
   @Cacheable(cacheNames = "getProductInfo"
           , key = "#productId")
    public ProductInfoDto getProductById(Long productId) {
    //   log.info("Getting product from DB for ID: {}", productId);
        Product product = productRepository.findByProductId(productId);

        if(product == null)
            throw new BusinessException(ECommerceExceptions.INVALID_PRODUCT);
        return convertToDTO(product);
    }

    public Optional<ProductInventory> getProductInventoryById(Long productId) {
        Optional<ProductInventory> productInventory =  productInventoryRepository.findById(productId);

        if (productInventory.isEmpty() || productInventory.get().getAmount() <= 0) {
            throw new BusinessException(ECommerceExceptions.OUT_OF_STOCK);
        }

        return productInventory;
    }

    public ProductInfoDto convertToDTO(Product product) {
        return new ProductInfoDto(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getCategory()
        );
    }
}
