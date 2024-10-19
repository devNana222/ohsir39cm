package com.tdd.ecommerce.cart.infrastructure;

import com.tdd.ecommerce.common.domain.TimeStamped;
import com.tdd.ecommerce.product.infrastructure.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@Table(name="cart")
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cart_id")
    private Long cartId;

    @Column(name="customer_id")
    private Long customerId;

    @Column(name="amount")
    private Long amount;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    public void addCartAmount(Long amount){
        if(amount < 0 && this.amount < -amount)
            throw new IllegalArgumentException("수량을 올바르게 입력하세요.");
        this.amount += amount;
    }
}
