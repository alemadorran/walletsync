package com.mymoney.walletsync.model.santander.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssociatedSantanderPaymentByMonthDTO {
    int month;
    List<AssociatedSantanderPaymentDTO> associatedSantanderPaymentDTOS;
}
