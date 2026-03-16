package com.jaycodesx.mortgage.infrastructure.admin;

import java.util.List;

public record BorrowerMetricsResponseDto(
        long totalBorrowers,
        double averageCreditScore,
        List<MetricSliceDto> creditScoreBands
) {
}
