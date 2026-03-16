package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import com.jaycodesx.mortgage.quote.model.LoanQuote;

import java.math.BigDecimal;
import java.util.UUID;

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

    public static QuoteJobMessage publicQuote(LoanQuote quote) {
        return new QuoteJobMessage(
                SCHEMA_VERSION,
                null,
                "PUBLIC_QUOTE",
                quote.getId(),
                quote.getSessionId(),
                null,
                quote.getHomePrice(),
                quote.getDownPayment(),
                quote.getZipCode(),
                quote.getLoanProgram(),
                quote.getPropertyUse(),
                quote.getTermYears(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static QuoteJobMessage refinedQuote(LoanQuote quote, Long borrowerQuoteProfileId, QuoteRefinementRequestDto request) {
        return new QuoteJobMessage(
                SCHEMA_VERSION,
                null,
                "REFINED_QUOTE",
                quote.getId(),
                quote.getSessionId(),
                borrowerQuoteProfileId,
                quote.getHomePrice(),
                quote.getDownPayment(),
                quote.getZipCode(),
                quote.getLoanProgram(),
                quote.getPropertyUse(),
                quote.getTermYears(),
                null,
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.annualIncome(),
                request.monthlyDebts(),
                request.creditScore(),
                request.cashReserves(),
                request.firstTimeBuyer(),
                request.vaEligible()
        );
    }

    public QuoteJobMessage withServiceToken(String token) {
        return new QuoteJobMessage(
                schemaVersion,
                messageId != null ? messageId : UUID.randomUUID().toString(),
                jobType,
                quoteId,
                sessionId,
                borrowerQuoteProfileId,
                homePrice,
                downPayment,
                zipCode,
                loanProgram,
                propertyUse,
                termYears,
                token,
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
}
