package com.mymoney.walletsync.model.santander.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryAmountDTO {
    private String categoryLabel;
    private BigDecimal totalAmount;

}
