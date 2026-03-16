package com.jaycodesx.mortgage.infrastructure.admin;

import java.math.BigDecimal;

public record AdminPricingProductRequestDto(
        String programCode,
        String productName,
        BigDecimal baseRate,
        boolean active
) {
}
