package com.jaycodesx.mortgage.quote.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class QuoteRefinementRequestDtoTest {

    @Test
    void exposesRecordValues() {
        QuoteRefinementRequestDto dto = new QuoteRefinementRequestDto(
                "Jay",
                "Lane",
                "jay@jaycodesx.dev",
                "555-111-0101",
                new BigDecimal("125000.00"),
                new BigDecimal("1100.00"),
                735,
                new BigDecimal("24000.00"),
                true,
                false
        );

        assertThat(dto.firstName()).isEqualTo("Jay");
        assertThat(dto.lastName()).isEqualTo("Lane");
        assertThat(dto.email()).isEqualTo("jay@jaycodesx.dev");
        assertThat(dto.phone()).isEqualTo("555-111-0101");
        assertThat(dto.annualIncome()).isEqualByComparingTo("125000.00");
        assertThat(dto.monthlyDebts()).isEqualByComparingTo("1100.00");
        assertThat(dto.creditScore()).isEqualTo(735);
        assertThat(dto.cashReserves()).isEqualByComparingTo("24000.00");
        assertThat(dto.firstTimeBuyer()).isTrue();
        assertThat(dto.vaEligible()).isFalse();
    }
}
