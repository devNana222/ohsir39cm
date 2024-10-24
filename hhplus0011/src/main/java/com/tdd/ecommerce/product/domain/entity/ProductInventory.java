package com.tdd.ecommerce.product.domain.entity;

import com.tdd.ecommerce.common.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="product_inventory")
public class ProductInventory extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(unique = true, nullable = false, name="product_id")
    private Long productId;

    @Column(name="amount")
    private Long amount;

    public void decreaseAmount(Long amount) {
        this.amount -= amount;
    }
}
