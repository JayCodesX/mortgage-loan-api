package com.jaycodesx.mortgage.infrastructure.metrics;

import com.jaycodesx.mortgage.lead.model.MortgageLead;
import com.jaycodesx.mortgage.lead.repository.MortgageLeadRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeadCatalogMetricsServiceTest {

    @Test
    void buildsLeadSnapshot() {
        MortgageLeadRepository repository = mock(MortgageLeadRepository.class);
        MortgageLead first = new MortgageLead();
        first.setLoanQuoteId(1L);
        first.setBorrowerQuoteProfileId(1L);
        first.setLeadStatus("NEW");
        first.setLeadSource("PUBLIC_QUOTE_FUNNEL");
        MortgageLead second = new MortgageLead();
        second.setLoanQuoteId(2L);
        second.setBorrowerQuoteProfileId(2L);
        second.setLeadStatus("CONTACTED");
        second.setLeadSource("PUBLIC_QUOTE_FUNNEL");
        when(repository.findAll()).thenReturn(List.of(first, second));

        LeadMetricsResponseDto snapshot = new LeadCatalogMetricsService(repository).getSnapshot();

        assertThat(snapshot.totalLeads()).isEqualTo(2);
        assertThat(snapshot.leadStatusDistribution()).hasSize(2);
        assertThat(snapshot.leadSourceDistribution()).singleElement().extracting(MetricSliceDto::value).isEqualTo(2L);
    }
}
