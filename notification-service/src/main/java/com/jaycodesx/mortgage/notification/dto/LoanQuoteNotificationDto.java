package com.jaycodesx.mortgage.notification.dto;

import com.jaycodesx.mortgage.lead.dto.MortgageLeadResponseDto;

import java.math.BigDecimal;

public record LoanQuoteNotificationDto(
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
        MortgageLeadResponseDto lead
) {
}
