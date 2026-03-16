package com.jaycodesx.mortgage.shared.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanCreateRequestDto(
        @NotNull Long borrowerId,
        @NotNull @Positive BigDecimal loanAmount,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal interestRate,
        @NotNull @Positive Integer termYears,
        @NotBlank String status
) {
}
