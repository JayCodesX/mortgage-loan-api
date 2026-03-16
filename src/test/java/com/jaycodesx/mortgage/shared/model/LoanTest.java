package com.jaycodesx.mortgage.shared.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanTest {

    @Test
    void constructorAndSettersPopulateFields() {
        Loan loan = new Loan(3L, 2L, new BigDecimal("325000.00"), new BigDecimal("6.1000"), 30, "PENDING");

        assertThat(loan.getId()).isEqualTo(3L);
        assertThat(loan.getBorrowerId()).isEqualTo(2L);
        assertThat(loan.getLoanAmount()).isEqualByComparingTo("325000.00");
        assertThat(loan.getInterestRate()).isEqualByComparingTo("6.1000");
        assertThat(loan.getTermYears()).isEqualTo(30);
        assertThat(loan.getStatus()).isEqualTo("PENDING");

        loan.setStatus("APPROVED");
        loan.setTermYears(15);

        assertThat(loan.getStatus()).isEqualTo("APPROVED");
        assertThat(loan.getTermYears()).isEqualTo(15);
    }
}
