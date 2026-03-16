package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.quote.model.LoanQuote;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class QuoteJobProcessorTest {

    @Test
    void keepsServiceTokenOnCopiedMessages() {
        LoanQuote quote = new LoanQuote();
        quote.setSessionId("session-5");
        quote.setHomePrice(new BigDecimal("525000.00"));
        quote.setDownPayment(new BigDecimal("105000.00"));
        quote.setZipCode("98101");
        quote.setLoanProgram("CONVENTIONAL");
        quote.setPropertyUse("PRIMARY_RESIDENCE");
        quote.setTermYears(30);

        QuoteJobMessage message = QuoteJobMessage.publicQuote(quote).withServiceToken("signed-token");

        assertThat(message.serviceToken()).isEqualTo("signed-token");
        assertThat(message.schemaVersion()).isEqualTo(QuoteJobMessage.SCHEMA_VERSION);
        assertThat(message.messageId()).isNotBlank();
        assertThat(message.homePrice()).isEqualByComparingTo("525000.00");
        assertThat(message.termYears()).isEqualTo(30);
    }
}
