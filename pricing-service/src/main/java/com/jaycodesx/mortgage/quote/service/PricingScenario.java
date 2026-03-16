package com.jaycodesx.mortgage.quote.service;

import java.math.BigDecimal;

public record PricingScenario(
        Long quoteId,
        BigDecimal homePrice,
        BigDecimal downPayment,
        String zipCode,
        String loanProgram,
        String propertyUse,
        Integer termYears
) {
}
