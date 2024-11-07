package com.tdd.ecommerce.product.application;

import com.tdd.ecommerce.product.domain.ProductInfoDto;
import com.tdd.ecommerce.product.domain.entity.Product;
import com.tdd.ecommerce.product.domain.entity.ProductInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;

    public ProductInfo getProductByProductId(Long productId) {

        ProductInfoDto product = productService.getProductById(productId);
        Optional<ProductInventory> productInventory = productService.getProductInventoryById(productId);

        return new ProductInfo(product.getProductId()
                                ,   product.getProductName()
                                ,   product.getPrice()
                                ,   productInventory.get().getAmount());

    }

    public List<ProductInfo> getProducts() {
        List<ProductServiceResponse> products = productService.getProducts();

        List<ProductInfo> productInfos = products.stream()
                .map(product -> new ProductInfo(
                        product.getProductId(),
                        product.getProductName(),
                        product.getPrice(),
                        product.getAmount()
                ))
                .collect(Collectors.toList());

        return productInfos;
    }

}
