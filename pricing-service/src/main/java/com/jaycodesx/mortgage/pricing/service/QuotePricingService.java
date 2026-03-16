package com.jaycodesx.mortgage.pricing.service;

import com.jaycodesx.mortgage.pricing.model.PricingAdjustmentRule;
import com.jaycodesx.mortgage.pricing.model.PricingProduct;
import com.jaycodesx.mortgage.pricing.model.RateSheet;
import com.jaycodesx.mortgage.pricing.repository.PricingAdjustmentRuleRepository;
import com.jaycodesx.mortgage.pricing.repository.PricingProductRepository;
import com.jaycodesx.mortgage.pricing.repository.RateSheetRepository;
import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import com.jaycodesx.mortgage.quote.service.PricingScenario;
import com.jaycodesx.mortgage.shared.service.MortgageMathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class QuotePricingService {

    private static final Logger log = LoggerFactory.getLogger(QuotePricingService.class);

    private final MortgageMathService mortgageMathService;
    private final PricingCacheService pricingCacheService;
    private final PricingProductRepository pricingProductRepository;
    private final RateSheetRepository rateSheetRepository;
    private final PricingAdjustmentRuleRepository pricingAdjustmentRuleRepository;

    public QuotePricingService(
            MortgageMathService mortgageMathService,
            PricingCacheService pricingCacheService,
            PricingProductRepository pricingProductRepository,
            RateSheetRepository rateSheetRepository,
            PricingAdjustmentRuleRepository pricingAdjustmentRuleRepository
    ) {
        this.mortgageMathService = mortgageMathService;
        this.pricingCacheService = pricingCacheService;
        this.pricingProductRepository = pricingProductRepository;
        this.rateSheetRepository = rateSheetRepository;
        this.pricingAdjustmentRuleRepository = pricingAdjustmentRuleRepository;
    }

    public QuoteDecision pricePublicQuote(PricingScenario quote) {
        Optional<QuoteDecision> cachedDecision = pricingCacheService.getPublicDecision(quote);
        if (cachedDecision.isPresent()) {
            log.info("Reused cached public pricing decision for zip={} program={} termYears={}",
                    quote.zipCode(), quote.loanProgram(), quote.termYears());
            return cachedDecision.get();
        }

        BigDecimal rate = baseRate(quote.loanProgram())
                .add(adjustForDownPayment(quote.homePrice(), quote.downPayment()))
                .add(adjustForTerm(quote.termYears()))
                .add(adjustForZip(quote.loanProgram(), quote.propertyUse(), quote.zipCode()));

        QuoteDecision decision = buildDecision(quote, rate, "Market Estimate");
        pricingCacheService.cachePublicDecision(quote, decision);
        return decision;
    }

    public QuoteDecision priceRefinedQuote(PricingScenario quote, QuoteRefinementRequestDto profile) {
        Optional<QuoteDecision> cachedDecision = pricingCacheService.getRefinedDecision(quote, profile);
        if (cachedDecision.isPresent()) {
            log.info("Reused cached refined pricing decision for quoteId={} creditScore={}",
                    quote.quoteId(), profile.creditScore());
            return cachedDecision.get();
        }

        BigDecimal rate = baseRate(quote.loanProgram())
                .add(adjustForDownPayment(quote.homePrice(), quote.downPayment()))
                .add(adjustForTerm(quote.termYears()))
                .add(adjustForZip(quote.loanProgram(), quote.propertyUse(), quote.zipCode()))
                .add(adjustForCreditScore(profile.creditScore()))
                .add(adjustForDebtRatio(profile.annualIncome(), profile.monthlyDebts()))
                .add(adjustForBorrowerFlags(quote.loanProgram(), profile.firstTimeBuyer(), profile.vaEligible()));

        QuoteDecision decision = buildDecision(quote, rate, qualificationTier(profile.creditScore(), profile.annualIncome(), profile.monthlyDebts()));
        pricingCacheService.cacheRefinedDecision(quote, profile, decision);
        return decision;
    }

    private QuoteDecision buildDecision(PricingScenario quote, BigDecimal rawRate, String tier) {
        BigDecimal rate = rawRate.max(new BigDecimal("4.7500")).setScale(4, RoundingMode.HALF_UP);
        BigDecimal financedAmount = quote.homePrice().subtract(quote.downPayment()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal apr = rate.add(new BigDecimal("0.1800")).setScale(4, RoundingMode.HALF_UP);
        BigDecimal cashToClose = quote.downPayment()
                .add(quote.homePrice().multiply(new BigDecimal("0.0210")))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = mortgageMathService.calculateMonthlyPayment(
                financedAmount,
                rate,
                quote.termYears() * 12
        );

        return new QuoteDecision(rate, apr, financedAmount, monthlyPayment, cashToClose, tier);
    }

    private BigDecimal baseRate(String loanProgram) {
        return pricingProductRepository.findByProgramCodeAndActiveTrue(normalize(loanProgram))
                .map(PricingProduct::getBaseRate)
                .orElse(new BigDecimal("6.3500"));
    }

    private BigDecimal adjustForDownPayment(BigDecimal homePrice, BigDecimal downPayment) {
        BigDecimal ratio = downPayment.divide(homePrice, 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(new BigDecimal("0.20")) >= 0) {
            return adjustment("DOWN_PAYMENT", "GE_20");
        }
        if (ratio.compareTo(new BigDecimal("0.10")) >= 0) {
            return adjustment("DOWN_PAYMENT", "GE_10");
        }
        if (ratio.compareTo(new BigDecimal("0.05")) >= 0) {
            return adjustment("DOWN_PAYMENT", "GE_5");
        }
        return adjustment("DOWN_PAYMENT", "LT_5");
    }

    private BigDecimal adjustForTerm(Integer termYears) {
        if (termYears <= 15) {
            return adjustment("TERM", "15");
        }
        if (termYears <= 20) {
            return adjustment("TERM", "20");
        }
        return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal adjustForZip(String loanProgram, String propertyUse, String zipCode) {
        String normalizedProgram = normalize(loanProgram);
        String normalizedPropertyUse = normalize(propertyUse);
        String zip = zipCode == null ? "" : zipCode.trim();

        return rateSheetRepository.findByProgramCodeAndPropertyUseAndActiveTrue(normalizedProgram, normalizedPropertyUse).stream()
                .filter(sheet -> zip.startsWith(sheet.getZipPrefix()))
                .max(Comparator.comparingInt(sheet -> sheet.getZipPrefix().length()))
                .map(RateSheet::getZipAdjustment)
                .orElse(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
    }

    private BigDecimal adjustForCreditScore(Integer creditScore) {
        if (creditScore >= 760) {
            return adjustment("CREDIT_SCORE", "GE_760");
        }
        if (creditScore >= 720) {
            return adjustment("CREDIT_SCORE", "GE_720");
        }
        if (creditScore >= 680) {
            return adjustment("CREDIT_SCORE", "GE_680");
        }
        if (creditScore >= 640) {
            return adjustment("CREDIT_SCORE", "GE_640");
        }
        return adjustment("CREDIT_SCORE", "LT_640");
    }

    private BigDecimal adjustForDebtRatio(BigDecimal annualIncome, BigDecimal monthlyDebts) {
        BigDecimal monthlyIncome = annualIncome.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
        BigDecimal ratio = monthlyDebts.divide(monthlyIncome, 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(new BigDecimal("0.43")) > 0) {
            return adjustment("DTI", "GT_43");
        }
        if (ratio.compareTo(new BigDecimal("0.36")) > 0) {
            return adjustment("DTI", "GT_36");
        }
        if (ratio.compareTo(new BigDecimal("0.28")) < 0) {
            return adjustment("DTI", "LT_28");
        }
        return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal adjustForBorrowerFlags(String loanProgram, Boolean firstTimeBuyer, Boolean vaEligible) {
        BigDecimal adjustment = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        if (Boolean.TRUE.equals(firstTimeBuyer)) {
            adjustment = adjustment.add(adjustment("FLAG", "FIRST_TIME_BUYER"));
        }
        if ("VA".equals(normalize(loanProgram)) && Boolean.TRUE.equals(vaEligible)) {
            adjustment = adjustment.add(adjustment("FLAG", "VA_ELIGIBLE"));
        }
        return adjustment;
    }

    private BigDecimal adjustment(String ruleType, String ruleKey) {
        return pricingAdjustmentRuleRepository.findByRuleTypeAndActiveTrue(ruleType).stream()
                .filter(rule -> ruleKey.equals(rule.getRuleKey()))
                .map(PricingAdjustmentRule::getAdjustment)
                .findFirst()
                .orElse(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
    }

    private String qualificationTier(Integer creditScore, BigDecimal annualIncome, BigDecimal monthlyDebts) {
        BigDecimal dti = monthlyDebts.divide(annualIncome.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP);
        if (creditScore >= 760 && dti.compareTo(new BigDecimal("0.33")) <= 0) {
            return "Prime+";
        }
        if (creditScore >= 700 && dti.compareTo(new BigDecimal("0.40")) <= 0) {
            return "Prime";
        }
        if (creditScore >= 640) {
            return "Near Prime";
        }
        return "Expanded Criteria";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    public record QuoteDecision(
            BigDecimal estimatedRate,
            BigDecimal estimatedApr,
            BigDecimal financedAmount,
            BigDecimal estimatedMonthlyPayment,
            BigDecimal estimatedCashToClose,
            String qualificationTier
    ) {
    }
}
