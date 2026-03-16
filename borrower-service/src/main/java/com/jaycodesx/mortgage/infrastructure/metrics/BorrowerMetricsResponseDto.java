package com.jaycodesx.mortgage.infrastructure.metrics;

import java.util.List;

public record BorrowerMetricsResponseDto(
        long totalBorrowers,
        double averageCreditScore,
        List<MetricSliceDto> creditScoreBands
) {
}
