package com.jaycodesx.mortgage.infrastructure.metrics;

import java.util.List;

public record LeadMetricsResponseDto(
        long totalLeads,
        List<MetricSliceDto> leadStatusDistribution,
        List<MetricSliceDto> leadSourceDistribution
) {
}
