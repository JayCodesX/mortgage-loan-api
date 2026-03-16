package com.jaycodesx.mortgage.quote.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BorrowerQuoteProfileTest {

    @Test
    void settersPopulateFieldsAndLifecycleSetsCreatedAt() {
        BorrowerQuoteProfile profile = new BorrowerQuoteProfile();
        profile.setLoanQuoteId(8L);
        profile.setFirstName("Jay");
        profile.setLastName("Stone");
        profile.setEmail("jay@jaycodesx.dev");
        profile.setPhone("555-111-0100");
        profile.setAnnualIncome(new BigDecimal("120000.00"));
        profile.setMonthlyDebts(new BigDecimal("950.00"));
        profile.setCreditScore(730);
        profile.setCashReserves(new BigDecimal("30000.00"));
        profile.setFirstTimeBuyer(true);
        profile.setVaEligible(false);

        profile.onCreate();

        assertThat(profile.getLoanQuoteId()).isEqualTo(8L);
        assertThat(profile.getFirstName()).isEqualTo("Jay");
        assertThat(profile.getAnnualIncome()).isEqualByComparingTo("120000.00");
        assertThat(profile.getCreditScore()).isEqualTo(730);
        assertThat(profile.getFirstTimeBuyer()).isTrue();
        assertThat(profile.getVaEligible()).isFalse();
        assertThat(profile.getCreatedAt()).isNotNull();
    }
}
