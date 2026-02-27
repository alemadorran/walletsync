package com.mymoney.walletsync.model.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String categoryName;
    private String categoryType; // Usamos String para facilitar el manejo del Enum
}
