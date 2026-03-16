package com.jaycodesx.mortgage.shared.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanCreateRequestDtoTest {

    @Test
    void exposesRecordValues() {
        LoanCreateRequestDto dto = new LoanCreateRequestDto(
                12L,
                new BigDecimal("325000.00"),
                new BigDecimal("6.1250"),
                30,
                "PENDING"
        );

        assertThat(dto.borrowerId()).isEqualTo(12L);
        assertThat(dto.loanAmount()).isEqualByComparingTo("325000.00");
        assertThat(dto.interestRate()).isEqualByComparingTo("6.1250");
        assertThat(dto.termYears()).isEqualTo(30);
        assertThat(dto.status()).isEqualTo("PENDING");
    }
}
