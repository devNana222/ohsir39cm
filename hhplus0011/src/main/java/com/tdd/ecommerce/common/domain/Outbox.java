package com.tdd.ecommerce.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "outbox")
public class Outbox extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="event_id")
    private Long eventId;

    @Column(name="event_type")
    private String eventType;

    @Setter
    @Column(name="status")
    private String status;

}
