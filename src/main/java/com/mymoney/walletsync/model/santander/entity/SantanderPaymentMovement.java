package com.mymoney.walletsync.model.santander.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "santander_payment_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SantanderPaymentMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate operationDate;

    private String operationLabel;

    @Column(precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(precision = 19, scale = 4)
    private BigDecimal balance;
}
