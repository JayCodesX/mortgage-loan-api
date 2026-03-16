package com.jaycodesx.mortgage.quote.service;

import java.math.BigDecimal;

public record PricingResultMessage(
        int schemaVersion,
        String messageId,
        Long quoteId,
        String processingStatus,
        String quoteStage,
        String quoteStatus,
        BigDecimal estimatedRate,
        BigDecimal estimatedApr,
        BigDecimal financedAmount,
        BigDecimal estimatedMonthlyPayment,
        BigDecimal estimatedCashToClose,
        String qualificationTier,
        String errorMessage,
        String serviceToken
) {
    public static final int SCHEMA_VERSION = 1;

    public boolean hasSupportedSchemaVersion() {
        return schemaVersion == SCHEMA_VERSION;
    }
}
