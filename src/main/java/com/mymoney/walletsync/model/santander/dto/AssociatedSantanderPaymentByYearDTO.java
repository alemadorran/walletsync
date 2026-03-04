package com.mymoney.walletsync.model.santander.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssociatedSantanderPaymentByYearDTO {
    Long year;
    List<AssociatedSantanderPaymentByMonthDTO> associatedSantanderPaymentByMonthDTOS;
}
