package com.jaycodesx.mortgage.lead.repository;

import com.jaycodesx.mortgage.lead.model.MortgageLead;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MortgageLeadRepositoryTest {

    @Autowired
    private MortgageLeadRepository mortgageLeadRepository;

    @Test
    void findsByLoanQuoteId() {
        MortgageLead lead = new MortgageLead();
        lead.setLoanQuoteId(33L);
        lead.setBorrowerQuoteProfileId(6L);
        lead.setLeadStatus("NEW");
        lead.setLeadSource("PUBLIC_QUOTE_FUNNEL");
        mortgageLeadRepository.save(lead);

        assertThat(mortgageLeadRepository.findByLoanQuoteId(33L)).isPresent();
    }
}
