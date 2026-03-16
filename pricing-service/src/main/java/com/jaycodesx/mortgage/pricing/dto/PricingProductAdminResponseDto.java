package com.jaycodesx.mortgage.pricing.dto;

import java.math.BigDecimal;

public record PricingProductAdminResponseDto(
        Long id,
        String programCode,
        String productName,
        BigDecimal baseRate,
        boolean active
) {
}
