package com.jaycodesx.mortgage.quote.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PublicLoanQuoteRequestDto(
        @NotNull @Positive BigDecimal homePrice,
        @NotNull @Min(0) BigDecimal downPayment,
        @NotBlank @Pattern(regexp = "\\d{5}", message = "zipCode must be 5 digits") String zipCode,
        @NotBlank String loanProgram,
        @NotBlank String propertyUse,
        @NotNull @Positive Integer termYears
) {
}
