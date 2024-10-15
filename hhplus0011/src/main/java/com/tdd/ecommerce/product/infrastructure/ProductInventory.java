package com.tdd.ecommerce.product.infrastructure;

import com.tdd.ecommerce.common.domain.TimeStamped;
import com.tdd.ecommerce.common.exception.BusinessException;
import com.tdd.ecommerce.common.exception.ECommerceExceptions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="product_inventory")
public class ProductInventory extends TimeStamped {
    @Id
    @GeneratedValue
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
