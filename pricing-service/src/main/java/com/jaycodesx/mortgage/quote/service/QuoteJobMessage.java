package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;

import java.math.BigDecimal;

public record QuoteJobMessage(
        int schemaVersion,
        String messageId,
        String jobType,
        Long quoteId,
        String sessionId,
        Long borrowerQuoteProfileId,
        BigDecimal homePrice,
        BigDecimal downPayment,
        String zipCode,
        String loanProgram,
        String propertyUse,
        Integer termYears,
        String serviceToken,
        String firstName,
        String lastName,
        String email,
        String phone,
        BigDecimal annualIncome,
        BigDecimal monthlyDebts,
        Integer creditScore,
        BigDecimal cashReserves,
        Boolean firstTimeBuyer,
        Boolean vaEligible
) {
    public static final int SCHEMA_VERSION = 1;

    public PricingScenario toPricingScenario() {
        return new PricingScenario(
                quoteId,
                homePrice,
                downPayment,
                zipCode,
                loanProgram,
                propertyUse,
                termYears
        );
    }

    public QuoteRefinementRequestDto toRefinementRequest() {
        return new QuoteRefinementRequestDto(
                firstName,
                lastName,
                email,
                phone,
                annualIncome,
                monthlyDebts,
                creditScore,
                cashReserves,
                firstTimeBuyer,
                vaEligible
        );
    }

    public boolean hasSupportedSchemaVersion() {
        return schemaVersion == SCHEMA_VERSION;
    }
}
