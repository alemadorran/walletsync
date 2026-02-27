package com.mymoney.walletsync.controller.common;

import com.mymoney.walletsync.model.common.dto.MovementAssociationDTO;
import com.mymoney.walletsync.services.common.MovementCategoryAssociationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/associations")
@RequiredArgsConstructor
public class MovementCategoryAssociationController {

    private final MovementCategoryAssociationService associationService;

    @GetMapping
    public ResponseEntity<List<MovementAssociationDTO>> getAll() {
        return ResponseEntity.ok(associationService.findAll());
    }

    @PostMapping
    public ResponseEntity<MovementAssociationDTO> create(@RequestBody MovementAssociationDTO dto) {
        // El DTO solo necesita { "associationWord": "Netflix", "categoryId": 5 }
        MovementAssociationDTO saved = associationService.save(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        associationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
