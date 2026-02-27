package com.mymoney.walletsync.controller.santander;

import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentDTO;
import com.mymoney.walletsync.model.santander.dto.SantanderPaymentMovementDTO;
import com.mymoney.walletsync.services.common.processor.AssociationProcessorService;
import com.mymoney.walletsync.services.santander.SantanderPaymentMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments/santander")
@RequiredArgsConstructor
public class SantanderPaymentMovementController {

    private final SantanderPaymentMovementService movementService;
    private final AssociationProcessorService associationProcessorService;

    @GetMapping("/inCategories")
    public ResponseEntity<List<AssociatedSantanderPaymentDTO>> getAllMovements() {
        List<SantanderPaymentMovementDTO> movements = movementService.findAll();
        List<AssociatedSantanderPaymentDTO> result = associationProcessorService.process(movements);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<SantanderPaymentMovementDTO>> getAllMovementsInCategories() {
        List<SantanderPaymentMovementDTO> movements = movementService.findAll();

        return ResponseEntity.ok(movements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SantanderPaymentMovementDTO> getMovementById(@PathVariable Long id) {
        return ResponseEntity.ok(movementService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SantanderPaymentMovementDTO> createMovement(@RequestBody SantanderPaymentMovementDTO dto) {
        SantanderPaymentMovementDTO savedMovement = movementService.save(dto);
        return new ResponseEntity<>(savedMovement, HttpStatus.CREATED);
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> createMovementList(@RequestBody List<SantanderPaymentMovementDTO> dto) {
        List<SantanderPaymentMovementDTO> savedMovementList = movementService.saveList(dto);
        return new ResponseEntity<>(savedMovementList, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SantanderPaymentMovementDTO> updateMovement(
            @PathVariable Long id,
            @RequestBody SantanderPaymentMovementDTO dto) {
        return ResponseEntity.ok(movementService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable Long id) {
        movementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}