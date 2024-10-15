package com.tdd.ecommerce.customer.infrastructure;

import com.tdd.ecommerce.common.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="customer")
public class Customer extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="customer_id")
    private Long customerId;

    @Column(name="cart_id")
    private Long cartId;

    @Column(name="balance")
    private Long balance;

    public Long chargeBalance(Long amount) {
        return this.balance += amount;
    }

    public void useBalance(Long amount) {
        this.balance -= amount;
    }
}
