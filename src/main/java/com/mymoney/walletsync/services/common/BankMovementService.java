package com.mymoney.walletsync.services.common;

import com.mymoney.walletsync.model.santander.dto.SantanderPaymentMovementDTO;

import java.util.List;

/**
 * Interfaz genérica para la gestión de movimientos bancarios.
 * @param <T> El tipo de DTO que maneja el banco específico.
 */
public interface BankMovementService<T> {

    List<T> findAll();

    T findById(Long id);

    T save(T dto);

    List<T> saveList(List<T> dtoList);

    T update(Long id, T dto);

    void delete(Long id);

    List<T> findByYear(Long year);

    List<SantanderPaymentMovementDTO> findExpensesByYear(Long year);

    List<SantanderPaymentMovementDTO> findIncomesByYear(Long year);
}