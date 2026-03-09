package com.example.mortgage.dto;

import java.math.BigDecimal;

public record MortgagePaymentResponseDto(
        BigDecimal loanAmount,
        BigDecimal downPayment,
        BigDecimal financedPrincipal,
        BigDecimal annualInterestRate,
        Integer termYears,
        Integer numberOfPayments,
        BigDecimal monthlyInterestRate,
        BigDecimal monthlyPayment
) {
}
