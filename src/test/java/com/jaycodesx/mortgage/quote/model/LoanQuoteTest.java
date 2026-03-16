package com.jaycodesx.mortgage.quote.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoanQuoteTest {

    @Test
    void settersPopulateFieldsAndLifecycleMethodsUpdateTimestamps() {
        LoanQuote quote = new LoanQuote();
        quote.setHomePrice(new BigDecimal("450000.00"));
        quote.setDownPayment(new BigDecimal("90000.00"));
        quote.setFinancedAmount(new BigDecimal("360000.00"));
        quote.setZipCode("60614");
        quote.setLoanProgram("CONVENTIONAL");
        quote.setPropertyUse("PRIMARY_RESIDENCE");
        quote.setTermYears(30);
        quote.setEstimatedRate(new BigDecimal("6.1500"));
        quote.setEstimatedApr(new BigDecimal("6.3300"));
        quote.setEstimatedMonthlyPayment(new BigDecimal("2198.12"));
        quote.setEstimatedCashToClose(new BigDecimal("99500.00"));
        quote.setQualificationTier("Prime");
        quote.setQuoteStage("PUBLIC");
        quote.setQuoteStatus("ESTIMATED");
        quote.setLeadCaptured(false);

        quote.onCreate();
        LocalDateTime createdAt = quote.getCreatedAt();
        quote.onUpdate();

        assertThat(quote.getHomePrice()).isEqualByComparingTo("450000.00");
        assertThat(quote.getZipCode()).isEqualTo("60614");
        assertThat(quote.getQualificationTier()).isEqualTo("Prime");
        assertThat(quote.isLeadCaptured()).isFalse();
        assertThat(createdAt).isNotNull();
        assertThat(quote.getUpdatedAt()).isNotNull();
        assertThat(quote.getUpdatedAt()).isAfterOrEqualTo(createdAt);
    }
}
