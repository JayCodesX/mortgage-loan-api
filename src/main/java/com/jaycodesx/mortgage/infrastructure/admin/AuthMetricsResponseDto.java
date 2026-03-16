package com.jaycodesx.mortgage.infrastructure.admin;

public record AuthMetricsResponseDto(
        long totalUsers,
        long adminUsers,
        long standardUsers
) {
}
