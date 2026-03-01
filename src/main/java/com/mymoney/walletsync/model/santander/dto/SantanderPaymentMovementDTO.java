package com.mymoney.walletsync.model.santander.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Record para transportar datos de movimientos de Santander.
 * Los records son inmutables y concisos.
 */
public record SantanderPaymentMovementDTO(
        Long id,
        LocalDate operationDate,
        String operationLabel,
        BigDecimal amount,
        BigDecimal balance
) {
}
