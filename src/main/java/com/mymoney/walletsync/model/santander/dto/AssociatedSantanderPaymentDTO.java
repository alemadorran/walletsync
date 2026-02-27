package com.mymoney.walletsync.model.santander.dto;

import com.mymoney.walletsync.model.common.dto.CategoryDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssociatedSantanderPaymentDTO {

    List<SantanderPaymentMovementDTO> movements;
    private CategoryDTO category;

    public AssociatedSantanderPaymentDTO(ArrayList<SantanderPaymentMovementDTO> santanderPaymentMovementDTOS, CategoryDTO categoryDTO) {
        this.movements = santanderPaymentMovementDTOS;
        this.category = categoryDTO;
    }
}
