package com.jaycodesx.mortgage.lead.service;

import com.jaycodesx.mortgage.infrastructure.metrics.LeadMetricsService;
import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
import com.jaycodesx.mortgage.lead.model.MortgageLead;
import com.jaycodesx.mortgage.lead.repository.MortgageLeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadJobProcessor {

    private final MortgageLeadRepository mortgageLeadRepository;
    private final ServiceTokenValidator serviceTokenValidator;
    private final LeadResultPublisher leadResultPublisher;
    private final LeadMetricsService leadMetricsService;

    public LeadJobProcessor(
            MortgageLeadRepository mortgageLeadRepository,
            ServiceTokenValidator serviceTokenValidator,
            LeadResultPublisher leadResultPublisher,
            LeadMetricsService leadMetricsService
    ) {
        this.mortgageLeadRepository = mortgageLeadRepository;
        this.serviceTokenValidator = serviceTokenValidator;
        this.leadResultPublisher = leadResultPublisher;
        this.leadMetricsService = leadMetricsService;
    }

    @Transactional
    public void process(LeadJobMessage message) {
        if (!message.hasSupportedSchemaVersion()) {
            throw new IllegalArgumentException("Unsupported lead job schema version: " + message.schemaVersion());
        }
        serviceTokenValidator.validateLeadToken(message.serviceToken());

        MortgageLead lead = mortgageLeadRepository.findByLoanQuoteId(message.loanQuoteId()).orElseGet(MortgageLead::new);
        lead.setLoanQuoteId(message.loanQuoteId());
        lead.setBorrowerQuoteProfileId(message.borrowerQuoteProfileId());
        lead.setLeadStatus(message.leadStatus());
        lead.setLeadSource(message.leadSource());
        mortgageLeadRepository.save(lead);
        leadResultPublisher.publish(new LeadResultMessage(
                LeadResultMessage.SCHEMA_VERSION,
                null,
                message.loanQuoteId(),
                message.borrowerQuoteProfileId(),
                message.leadStatus(),
                message.leadSource(),
                null
        ));
        leadMetricsService.recordLeadCreated(message.loanQuoteId());
    }
}
