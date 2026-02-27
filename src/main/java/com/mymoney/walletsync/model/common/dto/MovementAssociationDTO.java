package com.mymoney.walletsync.model.common.dto;

import lombok.Data;

@Data
public class MovementAssociationDTO {
    private Long id;
    private String associationWord;
    private Long categoryId; // Solo el ID para simplificar la creación desde el Frontend
    private String categoryName; // Para mostrar información al usuario sin cargar todo el objeto
}
