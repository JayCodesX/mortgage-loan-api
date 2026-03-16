package com.jaycodesx.mortgage.infrastructure.admin;

public record AdminReportQueryDto(
        String reportType,
        String search,
        Boolean activeOnly,
        String status,
        Integer minCreditScore,
        Integer maxCreditScore,
        String programCode
) {
}
