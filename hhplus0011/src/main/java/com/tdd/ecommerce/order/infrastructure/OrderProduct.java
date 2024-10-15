package com.tdd.ecommerce.order.infrastructure;

import com.tdd.ecommerce.common.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_product")
public class OrderProduct extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="order_id")
    private Long orderId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="amount")
    private Long amount;

    @Setter
    @Column(name="price")
    private Long price;

}
