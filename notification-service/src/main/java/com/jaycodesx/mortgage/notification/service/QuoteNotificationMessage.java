package com.jaycodesx.mortgage.notification.service;

import com.jaycodesx.mortgage.lead.dto.MortgageLeadResponseDto;
import com.jaycodesx.mortgage.notification.dto.LoanQuoteNotificationDto;

import java.math.BigDecimal;

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

    public LoanQuoteNotificationDto toDto() {
        return new LoanQuoteNotificationDto(
                id, sessionId, processingStatus, duplicate, quoteStage, quoteStatus, leadCaptured,
                borrowerProfileCaptured, homePrice, downPayment, financedAmount, zipCode,
                loanProgram, propertyUse, termYears, estimatedRate, estimatedApr,
                estimatedMonthlyPayment, estimatedCashToClose, qualificationTier, nextStep, lead
        );
    }

    public boolean hasSupportedSchemaVersion() {
        return schemaVersion == SCHEMA_VERSION;
    }
}
