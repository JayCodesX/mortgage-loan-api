package com.jaycodesx.mortgage.shared.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AmortizationCalculationResponseDtoTest {

    @Test
    void exposesRecordValues() {
        AmortizationCalculationResponseDto dto = new AmortizationCalculationResponseDto(
                new BigDecimal("250000.00"),
                new BigDecimal("0.00500000"),
                360,
                new BigDecimal("1498.88")
        );

        assertThat(dto.principal()).isEqualByComparingTo("250000.00");
        assertThat(dto.monthlyInterestRate()).isEqualByComparingTo("0.00500000");
        assertThat(dto.numberOfPayments()).isEqualTo(360);
        assertThat(dto.monthlyPayment()).isEqualByComparingTo("1498.88");
    }
}
