package com.mymoney.walletsync.model.santander.dto;

import com.mymoney.walletsync.model.common.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearReportDTO {
    private Long year;
    private CategoryType categoryType;
    private List<CategoryAmountDTO> totalAmountByCategoriesDTOS;

}
