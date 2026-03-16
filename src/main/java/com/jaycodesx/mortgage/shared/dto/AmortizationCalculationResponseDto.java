package com.jaycodesx.mortgage.shared.dto;

import java.math.BigDecimal;

public record AmortizationCalculationResponseDto(
        BigDecimal principal,
        BigDecimal monthlyInterestRate,
        Integer numberOfPayments,
        BigDecimal monthlyPayment
) {
}
