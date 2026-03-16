package com.jaycodesx.mortgage.infrastructure.admin;

import java.util.List;

public record LeadMetricsResponseDto(
        long totalLeads,
        List<MetricSliceDto> leadStatusDistribution,
        List<MetricSliceDto> leadSourceDistribution
) {
}
