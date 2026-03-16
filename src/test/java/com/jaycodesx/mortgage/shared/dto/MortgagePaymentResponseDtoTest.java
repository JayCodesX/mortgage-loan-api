package com.jaycodesx.mortgage.shared.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MortgagePaymentResponseDtoTest {

    @Test
    void exposesRecordValues() {
        MortgagePaymentResponseDto dto = new MortgagePaymentResponseDto(
                new BigDecimal("450000.00"),
                new BigDecimal("90000.00"),
                new BigDecimal("360000.00"),
                new BigDecimal("6.5000"),
                30,
                360,
                new BigDecimal("0.00541667"),
                new BigDecimal("2275.44")
        );

        assertThat(dto.loanAmount()).isEqualByComparingTo("450000.00");
        assertThat(dto.downPayment()).isEqualByComparingTo("90000.00");
        assertThat(dto.financedPrincipal()).isEqualByComparingTo("360000.00");
        assertThat(dto.annualInterestRate()).isEqualByComparingTo("6.5000");
        assertThat(dto.numberOfPayments()).isEqualTo(360);
        assertThat(dto.monthlyPayment()).isEqualByComparingTo("2275.44");
    }
}
