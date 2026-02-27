package com.mymoney.walletsync.services.common;

import com.mymoney.walletsync.model.common.dto.MovementAssociationDTO;
import com.mymoney.walletsync.model.common.entity.Category;
import com.mymoney.walletsync.model.common.entity.MovementCategoryAssociation;
import com.mymoney.walletsync.repository.CategoryRepository;
import com.mymoney.walletsync.repository.MovementCategoryAssociationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovementCategoryAssociationService {

    private final MovementCategoryAssociationRepository associationRepository;
    private final CategoryRepository categoryRepository;

    public List<MovementAssociationDTO> findAll() {
        return associationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovementAssociationDTO save(MovementAssociationDTO dto) {
        // Buscamos la categoría por el ID proporcionado en el DTO
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada para asociar"));

        MovementCategoryAssociation association = new MovementCategoryAssociation();
        association.setAssociationWord(dto.getAssociationWord());
        association.setCategory(category);

        return convertToDTO(associationRepository.save(association));
    }

    @Transactional
    public void delete(Long id) {
        if (!associationRepository.existsById(id)) {
            throw new RuntimeException("No existe la asociación con id: " + id);
        }
        associationRepository.deleteById(id);
    }

    // --- Helper Methods ---
    private MovementAssociationDTO convertToDTO(MovementCategoryAssociation entity) {
        MovementAssociationDTO dto = new MovementAssociationDTO();
        dto.setId(entity.getId());
        dto.setAssociationWord(entity.getAssociationWord());

        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getCategoryName());
        }
        return dto;
    }
}
