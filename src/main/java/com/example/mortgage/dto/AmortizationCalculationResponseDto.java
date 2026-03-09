package com.example.mortgage.dto;

import java.math.BigDecimal;

public record AmortizationCalculationResponseDto(
        BigDecimal principal,
        BigDecimal monthlyInterestRate,
        Integer numberOfPayments,
        BigDecimal monthlyPayment
) {
}
