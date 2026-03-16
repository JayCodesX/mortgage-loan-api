package com.jaycodesx.mortgage.infrastructure.metrics;

import java.util.List;

public record PricingMetricsResponseDto(
        long totalProducts,
        long activeProducts,
        long activeRateSheets,
        long activeAdjustmentRules,
        List<MetricSliceDto> programDistribution
) {
}
