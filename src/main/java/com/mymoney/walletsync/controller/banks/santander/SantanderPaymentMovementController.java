package com.mymoney.walletsync.controller.banks.santander;

import com.mymoney.walletsync.controller.banks.BankController;
import com.mymoney.walletsync.model.common.enums.CategoryType;
import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentByYearDTO;
import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentDTO;
import com.mymoney.walletsync.model.santander.dto.SantanderPaymentMovementDTO;
import com.mymoney.walletsync.model.santander.dto.YearReportDTO;
import com.mymoney.walletsync.services.common.BankMovementService;
import com.mymoney.walletsync.services.common.processor.AssociationProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/payments/santander")
@RequiredArgsConstructor
public class SantanderPaymentMovementController implements BankController<SantanderPaymentMovementDTO, AssociatedSantanderPaymentDTO, AssociatedSantanderPaymentByYearDTO> {

    private final BankMovementService<SantanderPaymentMovementDTO> santanderPaymentMovementService;
    private final AssociationProcessorService associationProcessorService;

    @Override
    public ResponseEntity<List<AssociatedSantanderPaymentDTO>> getAllMovementsWithCategories() {
        List<SantanderPaymentMovementDTO> movements = santanderPaymentMovementService.findAll();
        List<AssociatedSantanderPaymentDTO> result = associationProcessorService.process(movements, null);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity <AssociatedSantanderPaymentByYearDTO> getAllMovementsWithCategoriesByYear(Long year) {
        List<SantanderPaymentMovementDTO> movements = santanderPaymentMovementService.findByYear(year);
        if(movements.isEmpty()){
            return ResponseEntity.ok(new AssociatedSantanderPaymentByYearDTO(year, Collections.emptyList()));
        }
        AssociatedSantanderPaymentByYearDTO result = associationProcessorService.processByYear(movements, year, null);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<AssociatedSantanderPaymentByYearDTO> getAllExpensesWithCategoriesByYear(Long year) {
        List<SantanderPaymentMovementDTO> movements = santanderPaymentMovementService.findExpensesByYear(year);
        if(movements.isEmpty()){
            return ResponseEntity.ok(new AssociatedSantanderPaymentByYearDTO(year, Collections.emptyList()));
        }
        AssociatedSantanderPaymentByYearDTO result = associationProcessorService.processByYear(movements, year, CategoryType.EXPENSE);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<AssociatedSantanderPaymentByYearDTO> getAllIncomesWithCategoriesByYear(Long year) {
        List<SantanderPaymentMovementDTO> movements = santanderPaymentMovementService.findIncomesByYear(year);
        if(movements.isEmpty()){
            return ResponseEntity.ok(new AssociatedSantanderPaymentByYearDTO(year, Collections.emptyList()));
        }
        AssociatedSantanderPaymentByYearDTO result = associationProcessorService.processByYear(movements, year, CategoryType.INCOME);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<YearReportDTO> getIncomesYearReport(Long year) {
        List<SantanderPaymentMovementDTO> movements = santanderPaymentMovementService.findIncomesByYear(year);
        if(movements.isEmpty()){
            return ResponseEntity.ok(new YearReportDTO(year, CategoryType.INCOME, Collections.emptyList()));
        }
        YearReportDTO result = associationProcessorService.processReportByYear(movements, year, CategoryType.INCOME);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<YearReportDTO> getExpensesYearReport(Long year) {
        List<SantanderPaymentMovementDTO> movements = santanderPaymentMovementService.findExpensesByYear(year);
        if(movements.isEmpty()){
            return ResponseEntity.ok(new YearReportDTO(year, CategoryType.EXPENSE, Collections.emptyList()));
        }
        YearReportDTO result = associationProcessorService.processReportByYear(movements, year, CategoryType.EXPENSE);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<SantanderPaymentMovementDTO>> getAllMovements() {
        return ResponseEntity.ok(santanderPaymentMovementService.findAll());
    }

    @Override
    public ResponseEntity<SantanderPaymentMovementDTO> getMovementById(Long id) {
        return ResponseEntity.ok(santanderPaymentMovementService.findById(id));
    }

    @Override
    public ResponseEntity<SantanderPaymentMovementDTO> createMovement(SantanderPaymentMovementDTO dto) {
        return new ResponseEntity<>(santanderPaymentMovementService.save(dto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> createMovementList(List<SantanderPaymentMovementDTO> dtoList) {
        return new ResponseEntity<>(santanderPaymentMovementService.saveList(dtoList), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<SantanderPaymentMovementDTO> updateMovement(Long id, SantanderPaymentMovementDTO dto) {
        return ResponseEntity.ok(santanderPaymentMovementService.update(id, dto));
    }

    @Override
    public ResponseEntity<Void> deleteMovement(Long id) {
        santanderPaymentMovementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}