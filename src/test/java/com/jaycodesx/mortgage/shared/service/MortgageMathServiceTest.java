package com.jaycodesx.mortgage.shared.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MortgageMathServiceTest {

    private final MortgageMathService mortgageMathService = new MortgageMathService();

    @Test
    void convertsAnnualRateToMonthlyRate() {
        BigDecimal monthlyRate = mortgageMathService.toMonthlyRate(new BigDecimal("6.0000"));

        assertThat(monthlyRate).isEqualByComparingTo("0.0050000000");
    }

    @Test
    void calculatesMonthlyPaymentForAnnualRate() {
        BigDecimal payment = mortgageMathService.calculateMonthlyPayment(
                new BigDecimal("300000.00"),
                new BigDecimal("6.5000"),
                360
        );

        assertThat(payment).isEqualByComparingTo("1896.20");
    }

    @Test
    void calculatesMonthlyPaymentForZeroMonthlyRate() {
        BigDecimal payment = mortgageMathService.calculateMonthlyPaymentForMonthlyRate(
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                120
        );

        assertThat(payment).isEqualByComparingTo("1000.00");
    }
}
