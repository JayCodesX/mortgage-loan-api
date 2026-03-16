package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.lead.model.MortgageLead;
import com.jaycodesx.mortgage.lead.repository.MortgageLeadRepository;
import com.jaycodesx.mortgage.pricing.service.QuotePricingService;
import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import com.jaycodesx.mortgage.quote.model.BorrowerQuoteProfile;
import com.jaycodesx.mortgage.quote.model.LoanQuote;
import com.jaycodesx.mortgage.quote.repository.BorrowerQuoteProfileRepository;
import com.jaycodesx.mortgage.quote.repository.LoanQuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;

@Service
public class QuoteJobProcessor {

    private final LoanQuoteRepository loanQuoteRepository;
    private final BorrowerQuoteProfileRepository borrowerQuoteProfileRepository;
    private final MortgageLeadRepository mortgageLeadRepository;
    private final QuotePricingService quotePricingService;
    private final QuoteSessionService quoteSessionService;

    public QuoteJobProcessor(
            LoanQuoteRepository loanQuoteRepository,
            BorrowerQuoteProfileRepository borrowerQuoteProfileRepository,
            MortgageLeadRepository mortgageLeadRepository,
            QuotePricingService quotePricingService,
            QuoteSessionService quoteSessionService
    ) {
        this.loanQuoteRepository = loanQuoteRepository;
        this.borrowerQuoteProfileRepository = borrowerQuoteProfileRepository;
        this.mortgageLeadRepository = mortgageLeadRepository;
        this.quotePricingService = quotePricingService;
        this.quoteSessionService = quoteSessionService;
    }

    @Transactional
    public void process(QuoteJobMessage message) {
        LoanQuote quote = loanQuoteRepository.findById(message.quoteId())
                .orElseThrow(() -> new IllegalArgumentException("Quote not found for id: " + message.quoteId()));

        updateStatus(quote, "PROCESSING", quote.getQuoteStatus());

        if ("PUBLIC_QUOTE".equals(message.jobType())) {
            QuotePricingService.QuoteDecision decision = quotePricingService.pricePublicQuote(quote);
            applyDecision(quote, decision);
            quote.setQuoteStatus("ESTIMATED");
            updateStatus(quote, "COMPLETED", quote.getQuoteStatus());
            return;
        }

        QuoteRefinementRequestDto request = message.toRefinementRequest();
        QuotePricingService.QuoteDecision decision = quotePricingService.priceRefinedQuote(quote, request);
        applyDecision(quote, decision);
        quote.setQuoteStage("REFINED");
        quote.setQuoteStatus("LEAD_READY");
        quote.setLeadCaptured(true);
        loanQuoteRepository.save(quote);

        BorrowerQuoteProfile profile = borrowerQuoteProfileRepository.findByLoanQuoteId(quote.getId())
                .orElseGet(BorrowerQuoteProfile::new);
        profile.setLoanQuoteId(quote.getId());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setEmail(request.email());
        profile.setPhone(request.phone());
        profile.setAnnualIncome(request.annualIncome().setScale(2, RoundingMode.HALF_UP));
        profile.setMonthlyDebts(request.monthlyDebts().setScale(2, RoundingMode.HALF_UP));
        profile.setCreditScore(request.creditScore());
        profile.setCashReserves(request.cashReserves().setScale(2, RoundingMode.HALF_UP));
        profile.setFirstTimeBuyer(request.firstTimeBuyer());
        profile.setVaEligible(request.vaEligible());
        BorrowerQuoteProfile savedProfile = borrowerQuoteProfileRepository.save(profile);

        MortgageLead lead = mortgageLeadRepository.findByLoanQuoteId(quote.getId()).orElseGet(MortgageLead::new);
        lead.setLoanQuoteId(quote.getId());
        lead.setBorrowerQuoteProfileId(savedProfile.getId());
        lead.setLeadStatus("NEW");
        lead.setLeadSource("PUBLIC_QUOTE_FUNNEL");
        mortgageLeadRepository.save(lead);

        updateStatus(quote, "COMPLETED", quote.getQuoteStatus());
    }

    @Transactional
    public void fail(Long quoteId, String message) {
        loanQuoteRepository.findById(quoteId).ifPresent(quote -> updateStatus(quote, "FAILED", "FAILED"));
    }

    private void updateStatus(LoanQuote quote, String processingStatus, String quoteStatus) {
        quote.setProcessingStatus(processingStatus);
        quote.setQuoteStatus(quoteStatus);
        loanQuoteRepository.save(quote);
        quoteSessionService.cacheQuoteStatus(quote.getId(), processingStatus);
    }

    private void applyDecision(LoanQuote quote, QuotePricingService.QuoteDecision decision) {
        quote.setEstimatedRate(decision.estimatedRate());
        quote.setEstimatedApr(decision.estimatedApr());
        quote.setFinancedAmount(decision.financedAmount());
        quote.setEstimatedMonthlyPayment(decision.estimatedMonthlyPayment());
        quote.setEstimatedCashToClose(decision.estimatedCashToClose());
        quote.setQualificationTier(decision.qualificationTier());
    }
}
