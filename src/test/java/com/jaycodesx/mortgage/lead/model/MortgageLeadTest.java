package com.jaycodesx.mortgage.lead.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MortgageLeadTest {

    @Test
    void settersPopulateFieldsAndLifecycleSetsCreatedAt() {
        MortgageLead lead = new MortgageLead();
        lead.setLoanQuoteId(7L);
        lead.setBorrowerQuoteProfileId(12L);
        lead.setLeadStatus("NEW");
        lead.setLeadSource("PUBLIC_QUOTE_FUNNEL");

        lead.onCreate();

        assertThat(lead.getLoanQuoteId()).isEqualTo(7L);
        assertThat(lead.getBorrowerQuoteProfileId()).isEqualTo(12L);
        assertThat(lead.getLeadStatus()).isEqualTo("NEW");
        assertThat(lead.getLeadSource()).isEqualTo("PUBLIC_QUOTE_FUNNEL");
        assertThat(lead.getCreatedAt()).isNotNull();
    }
}
