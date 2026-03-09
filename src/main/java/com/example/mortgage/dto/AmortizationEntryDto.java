package com.example.mortgage.dto;

import java.math.BigDecimal;

public record AmortizationEntryDto(
        int paymentNumber,
        BigDecimal paymentAmount,
        BigDecimal principalPaid,
        BigDecimal interestPaid,
        BigDecimal remainingBalance
) {
}
