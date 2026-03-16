package com.jaycodesx.mortgage.infrastructure.metrics;

public record QuoteMetricsResponseDto(
        long quotesStarted,
        long quoteRefinementsRequested,
        long quotesDeduped,
        long quotesCompleted,
        long quotesFailed,
        long leadsCreated,
        long averagePricingDurationMs,
        long averageLeadDurationMs,
        long sessionsWithQuotes,
        long authenticatedSessions,
        long sessionsWithRefinements,
        long leadConvertedSessions,
        long authRegistrations,
        long authLogins,
        double quoteToAuthConversionRate,
        double authToRefinementConversionRate,
        double quoteToLeadConversionRate,
        long pricingResultMessagesDeduped,
        long leadResultMessagesDeduped,
        long pricingResultDlqPublishes,
        long leadResultDlqPublishes
) {
}
