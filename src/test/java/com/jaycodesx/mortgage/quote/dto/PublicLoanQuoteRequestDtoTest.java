package com.jaycodesx.mortgage.quote.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PublicLoanQuoteRequestDtoTest {

    @Test
    void exposesRecordValues() {
        PublicLoanQuoteRequestDto dto = new PublicLoanQuoteRequestDto(
                new BigDecimal("475000.00"),
                new BigDecimal("85000.00"),
                "60614",
                "CONVENTIONAL",
                "PRIMARY_RESIDENCE",
                30
        );

        assertThat(dto.homePrice()).isEqualByComparingTo("475000.00");
        assertThat(dto.downPayment()).isEqualByComparingTo("85000.00");
        assertThat(dto.zipCode()).isEqualTo("60614");
        assertThat(dto.loanProgram()).isEqualTo("CONVENTIONAL");
        assertThat(dto.propertyUse()).isEqualTo("PRIMARY_RESIDENCE");
        assertThat(dto.termYears()).isEqualTo(30);
    }
}
