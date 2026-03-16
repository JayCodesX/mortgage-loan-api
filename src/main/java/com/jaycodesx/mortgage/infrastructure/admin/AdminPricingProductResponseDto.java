package com.jaycodesx.mortgage.infrastructure.admin;

import java.math.BigDecimal;

public record AdminPricingProductResponseDto(
        Long id,
        String programCode,
        String productName,
        BigDecimal baseRate,
        boolean active
) {
}
