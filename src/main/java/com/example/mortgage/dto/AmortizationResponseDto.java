package com.example.mortgage.dto;

import java.math.BigDecimal;
import java.util.List;

public record AmortizationResponseDto(
        Long loanId,
        Long borrowerId,
        BigDecimal loanAmount,
        BigDecimal interestRate,
        Integer termYears,
        BigDecimal monthlyPayment,
        List<AmortizationEntryDto> schedule
) {
}
