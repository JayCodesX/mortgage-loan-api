package com.jaycodesx.mortgage.infrastructure.admin;

import java.util.List;
import java.util.Map;

public record AdminReportResponseDto(
        String title,
        List<String> columns,
        List<Map<String, Object>> rows,
        int totalRows
) {
}
