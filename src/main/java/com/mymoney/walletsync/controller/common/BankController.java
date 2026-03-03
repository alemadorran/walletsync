package com.mymoney.walletsync.controller.common;

import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentByYearDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

public interface BankController<T, A, Z> {

    @GetMapping("/inCategories")
    ResponseEntity<List<A>> getAllMovementsWithCategories();

    @GetMapping("/inCategoriesByYear/{year}")
    ResponseEntity<Z> getAllMovementsWithCategoriesByYear(@PathVariable Long year);

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