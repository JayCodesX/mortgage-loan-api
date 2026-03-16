package com.jaycodesx.mortgage.lead.service;

import com.jaycodesx.mortgage.infrastructure.metrics.LeadMetricsService;
import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
import com.jaycodesx.mortgage.lead.model.MortgageLead;
import com.jaycodesx.mortgage.lead.repository.MortgageLeadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadJobProcessorTest {

    @Mock
    private MortgageLeadRepository mortgageLeadRepository;
    @Mock
    private ServiceTokenValidator serviceTokenValidator;
    @Mock
    private LeadResultPublisher leadResultPublisher;
    @Mock
    private LeadMetricsService leadMetricsService;

    @InjectMocks
    private LeadJobProcessor leadJobProcessor;

    @Test
    void persistsLeadAndMarksQuoteCaptured() throws Exception {
        when(mortgageLeadRepository.findByLoanQuoteId(11L)).thenReturn(Optional.empty());
        when(mortgageLeadRepository.save(any(MortgageLead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadJobProcessor.process(new LeadJobMessage(
                LeadJobMessage.SCHEMA_VERSION,
                "lead-job-msg-1",
                11L,
                21L,
                "NEW",
                "PUBLIC_QUOTE_FUNNEL",
                "signed-token"
        ));

        verify(serviceTokenValidator).validateLeadToken("signed-token");
        verify(mortgageLeadRepository).save(any(MortgageLead.class));
        verify(leadResultPublisher).publish(any());
        verify(leadMetricsService).recordLeadCreated(11L);
    }
}
