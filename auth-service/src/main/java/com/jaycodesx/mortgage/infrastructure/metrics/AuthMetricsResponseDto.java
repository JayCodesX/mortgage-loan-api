package com.jaycodesx.mortgage.infrastructure.metrics;

public record AuthMetricsResponseDto(
        long totalUsers,
        long adminUsers,
        long standardUsers
) {
}
