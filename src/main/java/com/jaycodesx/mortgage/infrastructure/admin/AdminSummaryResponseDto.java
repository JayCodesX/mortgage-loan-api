package com.jaycodesx.mortgage.infrastructure.admin;

import com.jaycodesx.mortgage.infrastructure.metrics.QuoteMetricsResponseDto;

public record AdminSummaryResponseDto(
        QuoteMetricsResponseDto quotes,
        AuthMetricsResponseDto auth,
        BorrowerMetricsResponseDto borrowers,
        PricingMetricsResponseDto pricing,
        LeadMetricsResponseDto leads,
        NotificationMetricsResponseDto notifications
) {
}
