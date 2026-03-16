package com.jaycodesx.mortgage.shared.dto;

import java.math.BigDecimal;

public record AmortizationEntryDto(
        int paymentNumber,
        BigDecimal paymentAmount,
        BigDecimal principalPaid,
        BigDecimal interestPaid,
        BigDecimal remainingBalance
) {
}
