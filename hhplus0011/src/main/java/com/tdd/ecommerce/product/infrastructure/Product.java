package com.tdd.ecommerce.product.infrastructure;

import com.tdd.ecommerce.cart.infrastructure.Cart;
import com.tdd.ecommerce.common.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="product")
public class Product extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name="product_id")
    private Long productId;

    @Column(name="product_nm")
    private String productName;

    @Column(name="price")
    private Long price;

    @Column(name="category")
    private String category;

    @OneToMany(mappedBy="product")
    private List<Cart> carts;

}