package com.mymoney.walletsync.services.santander;

import com.mymoney.walletsync.model.santander.dto.SantanderPaymentMovementDTO;
import com.mymoney.walletsync.model.santander.entity.SantanderPaymentMovement;
import com.mymoney.walletsync.repository.SantanderPaymentMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SantanderPaymentMovementService {

    private final SantanderPaymentMovementRepository repository;

    @Transactional(readOnly = true)
    public List<SantanderPaymentMovementDTO> findAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .toList(); // En Java 16+ .toList() es más directo
    }

    @Transactional(readOnly = true)
    public SantanderPaymentMovementDTO findById(Long id) {
        return repository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));
    }

    @Transactional
    public SantanderPaymentMovementDTO save(SantanderPaymentMovementDTO dto) {
        SantanderPaymentMovement entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    @Transactional
    public List<SantanderPaymentMovementDTO> saveList(List<SantanderPaymentMovementDTO> dto) {
        List<SantanderPaymentMovementDTO> listToReturn = new ArrayList<>();
        for (SantanderPaymentMovementDTO dtoToSave : dto) {
            listToReturn.add(save(dtoToSave));
        }
        return listToReturn;
    }

    @Transactional
    public SantanderPaymentMovementDTO update(Long id, SantanderPaymentMovementDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setOperationDate(dto.operationDate());
            existing.setOperationLabel(dto.operationLabel());
            existing.setAmount(dto.amount());
            existing.setBalance(dto.balance());
            return convertToDTO(repository.save(existing));
        }).orElseThrow(() -> new RuntimeException("No se encontró el registro para actualizar"));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("No existe el movimiento con id: " + id);
        }
        repository.deleteById(id);
    }

    // --- Métodos de Mapeo ---

    private SantanderPaymentMovementDTO convertToDTO(SantanderPaymentMovement entity) {
        return new SantanderPaymentMovementDTO(
                entity.getId(),
                entity.getOperationDate(),
                entity.getOperationLabel(),
                entity.getAmount(),
                entity.getBalance()
        );
    }

    private SantanderPaymentMovement convertToEntity(SantanderPaymentMovementDTO dto) {
        SantanderPaymentMovement entity = new SantanderPaymentMovement();
        entity.setId(dto.id());
        entity.setOperationDate(dto.operationDate());
        entity.setOperationLabel(dto.operationLabel());
        entity.setAmount(dto.amount());
        entity.setBalance(dto.balance());
        return entity;
    }
}