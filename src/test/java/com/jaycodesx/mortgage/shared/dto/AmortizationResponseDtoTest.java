package com.jaycodesx.mortgage.shared.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AmortizationResponseDtoTest {

    @Test
    void exposesRecordValues() {
        AmortizationEntryDto entry = new AmortizationEntryDto(
                1,
                new BigDecimal("1600.00"),
                new BigDecimal("400.00"),
                new BigDecimal("1200.00"),
                new BigDecimal("299600.00")
        );
        AmortizationResponseDto dto = new AmortizationResponseDto(
                10L,
                4L,
                new BigDecimal("300000.00"),
                new BigDecimal("6.5000"),
                30,
                new BigDecimal("1896.20"),
                List.of(entry)
        );

        assertThat(dto.loanId()).isEqualTo(10L);
        assertThat(dto.borrowerId()).isEqualTo(4L);
        assertThat(dto.loanAmount()).isEqualByComparingTo("300000.00");
        assertThat(dto.interestRate()).isEqualByComparingTo("6.5000");
        assertThat(dto.termYears()).isEqualTo(30);
        assertThat(dto.monthlyPayment()).isEqualByComparingTo("1896.20");
        assertThat(dto.schedule()).containsExactly(entry);
    }
}
