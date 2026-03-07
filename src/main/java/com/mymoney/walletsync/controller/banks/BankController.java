package com.mymoney.walletsync.controller.banks;

import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentDTO;
import com.mymoney.walletsync.model.santander.dto.YearReportDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

public interface BankController<T, A, Z> {

    @GetMapping("/inCategories")
    ResponseEntity<List<A>> getAllMovementsWithCategories();

    @GetMapping("/inCategoriesByYear/{year}")
    ResponseEntity<Z> getAllMovementsWithCategoriesByYear(@PathVariable Long year);

    @GetMapping("/expenses/inCategoriesByYear/{year}")
    ResponseEntity<Z> getAllExpensesWithCategoriesByYear(@PathVariable Long year);

    @GetMapping("/incomes/inCategoriesByYear/{year}")
    ResponseEntity<Z> getAllIncomesWithCategoriesByYear(@PathVariable Long year);

    @GetMapping("/incomes/yearReport/{year}")
    ResponseEntity<YearReportDTO> getIncomesYearReport(@PathVariable Long year);

    @GetMapping("/expenses/yearReport/{year}")
    ResponseEntity<YearReportDTO> getExpensesYearReport(@PathVariable Long year);

    @GetMapping
    ResponseEntity<List<T>> getAllMovements();

    @GetMapping("/{id}")
    ResponseEntity<T> getMovementById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<T> createMovement(@RequestBody T dto);

    @PostMapping("/saveAll")
    ResponseEntity<?> createMovementList(@RequestBody List<T> dtoList);

    @PutMapping("/{id}")
    ResponseEntity<T> updateMovement(@PathVariable Long id, @RequestBody T dto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteMovement(@PathVariable Long id);
}