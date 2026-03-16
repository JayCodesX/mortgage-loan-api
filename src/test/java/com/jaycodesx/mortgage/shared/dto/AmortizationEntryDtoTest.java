package com.jaycodesx.mortgage.shared.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AmortizationEntryDtoTest {

    @Test
    void exposesRecordValues() {
        AmortizationEntryDto dto = new AmortizationEntryDto(
                12,
                new BigDecimal("1500.00"),
                new BigDecimal("525.00"),
                new BigDecimal("975.00"),
                new BigDecimal("240000.00")
        );

        assertThat(dto.paymentNumber()).isEqualTo(12);
        assertThat(dto.paymentAmount()).isEqualByComparingTo("1500.00");
        assertThat(dto.principalPaid()).isEqualByComparingTo("525.00");
        assertThat(dto.interestPaid()).isEqualByComparingTo("975.00");
        assertThat(dto.remainingBalance()).isEqualByComparingTo("240000.00");
    }
}
