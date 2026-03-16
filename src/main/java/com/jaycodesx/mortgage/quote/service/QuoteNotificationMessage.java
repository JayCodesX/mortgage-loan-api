package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.lead.dto.MortgageLeadResponseDto;
import com.jaycodesx.mortgage.quote.dto.LoanQuoteResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteNotificationMessage(
        int schemaVersion,
        String messageId,
        Long id,
        String sessionId,
        String processingStatus,
        boolean duplicate,
        String quoteStage,
        String quoteStatus,
        boolean leadCaptured,
        boolean borrowerProfileCaptured,
        BigDecimal homePrice,
        BigDecimal downPayment,
        BigDecimal financedAmount,
        String zipCode,
        String loanProgram,
        String propertyUse,
        Integer termYears,
        BigDecimal estimatedRate,
        BigDecimal estimatedApr,
        BigDecimal estimatedMonthlyPayment,
        BigDecimal estimatedCashToClose,
        String qualificationTier,
        String nextStep,
        MortgageLeadResponseDto lead,
        String serviceToken
) {
    public static final int SCHEMA_VERSION = 1;

    public static QuoteNotificationMessage fromResponse(LoanQuoteResponseDto response) {
        return new QuoteNotificationMessage(
                SCHEMA_VERSION,
                null,
                response.id(),
                response.sessionId(),
                response.processingStatus(),
                response.duplicate(),
                response.quoteStage(),
                response.quoteStatus(),
                response.leadCaptured(),
                response.borrowerProfileCaptured(),
                response.homePrice(),
                response.downPayment(),
                response.financedAmount(),
                response.zipCode(),
                response.loanProgram(),
                response.propertyUse(),
                response.termYears(),
                response.estimatedRate(),
                response.estimatedApr(),
                response.estimatedMonthlyPayment(),
                response.estimatedCashToClose(),
                response.qualificationTier(),
                response.nextStep(),
                response.lead(),
                null
        );
    }

    public QuoteNotificationMessage withServiceToken(String token) {
        return new QuoteNotificationMessage(
                schemaVersion,
                messageId != null ? messageId : UUID.randomUUID().toString(),
                id, sessionId, processingStatus, duplicate, quoteStage, quoteStatus, leadCaptured,
                borrowerProfileCaptured, homePrice, downPayment, financedAmount, zipCode, loanProgram,
                propertyUse, termYears, estimatedRate, estimatedApr, estimatedMonthlyPayment,
                estimatedCashToClose, qualificationTier, nextStep, lead, token
        );
    }

    public boolean hasSupportedSchemaVersion() {
        return schemaVersion == SCHEMA_VERSION;
    }
}
