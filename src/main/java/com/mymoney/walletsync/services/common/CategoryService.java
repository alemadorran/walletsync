package com.mymoney.walletsync.services.common;

import com.mymoney.walletsync.model.common.dto.CategoryDTO;
import com.mymoney.walletsync.model.common.entity.Category;
import com.mymoney.walletsync.model.common.enums.CategoryType;
import com.mymoney.walletsync.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return convertToDTO(category);
    }

    public List<CategoryDTO> findByCategoryType(CategoryType categoryType) {
        return categoryRepository.findByCategoryType(categoryType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO save(CategoryDTO dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setCategoryType(CategoryType.valueOf(dto.getCategoryType()));

        return convertToDTO(categoryRepository.save(category));
    }

    @Transactional
    public boolean delete(Long id) {
        try{
            categoryRepository.deleteById(id);
            return true;
        }catch(EmptyResultDataAccessException e){
            return false;
        }
    }

    // --- Helper Methods ---
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setCategoryType(category.getCategoryType().name());
        return dto;
    }
}
