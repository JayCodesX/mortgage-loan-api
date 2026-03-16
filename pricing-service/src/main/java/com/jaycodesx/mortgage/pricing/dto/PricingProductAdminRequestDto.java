package com.jaycodesx.mortgage.pricing.dto;

import java.math.BigDecimal;

public record PricingProductAdminRequestDto(
        String programCode,
        String productName,
        BigDecimal baseRate,
        boolean active
) {
}
