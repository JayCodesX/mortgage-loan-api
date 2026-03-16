package com.jaycodesx.mortgage.pricing.service;

import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import com.jaycodesx.mortgage.quote.model.LoanQuote;
import com.jaycodesx.mortgage.shared.service.MortgageMathService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class QuotePricingService {

    private static final Map<String, BigDecimal> PROGRAM_RATES = Map.of(
            "CONVENTIONAL", new BigDecimal("6.2500"),
            "FHA", new BigDecimal("6.0500"),
            "VA", new BigDecimal("5.9500"),
            "JUMBO", new BigDecimal("6.5500")
    );

    private final MortgageMathService mortgageMathService;

    public QuotePricingService(MortgageMathService mortgageMathService) {
        this.mortgageMathService = mortgageMathService;
    }

    public QuoteDecision pricePublicQuote(LoanQuote quote) {
        BigDecimal rate = baseRate(quote.getLoanProgram())
                .add(adjustForDownPayment(quote.getHomePrice(), quote.getDownPayment()))
                .add(adjustForTerm(quote.getTermYears()))
                .add(adjustForZip(quote.getZipCode()));

        return buildDecision(quote, rate, "Market Estimate");
    }

    public QuoteDecision priceRefinedQuote(LoanQuote quote, QuoteRefinementRequestDto profile) {
        BigDecimal rate = baseRate(quote.getLoanProgram())
                .add(adjustForDownPayment(quote.getHomePrice(), quote.getDownPayment()))
                .add(adjustForTerm(quote.getTermYears()))
                .add(adjustForZip(quote.getZipCode()))
                .add(adjustForCreditScore(profile.creditScore()))
                .add(adjustForDebtRatio(profile.annualIncome(), profile.monthlyDebts()))
                .add(adjustForBorrowerFlags(quote.getLoanProgram(), profile.firstTimeBuyer(), profile.vaEligible()));

        return buildDecision(quote, rate, qualificationTier(profile.creditScore(), profile.annualIncome(), profile.monthlyDebts()));
    }

    private QuoteDecision buildDecision(LoanQuote quote, BigDecimal rawRate, String tier) {
        BigDecimal rate = rawRate.max(new BigDecimal("4.7500")).setScale(4, RoundingMode.HALF_UP);
        BigDecimal financedAmount = quote.getHomePrice().subtract(quote.getDownPayment()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal apr = rate.add(new BigDecimal("0.1800")).setScale(4, RoundingMode.HALF_UP);
        BigDecimal cashToClose = quote.getDownPayment()
                .add(quote.getHomePrice().multiply(new BigDecimal("0.0210")))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = mortgageMathService.calculateMonthlyPayment(
                financedAmount,
                rate,
                quote.getTermYears() * 12
        );

        return new QuoteDecision(rate, apr, financedAmount, monthlyPayment, cashToClose, tier);
    }

    private BigDecimal baseRate(String loanProgram) {
        return PROGRAM_RATES.getOrDefault(normalize(loanProgram), new BigDecimal("6.3500"));
    }

    private BigDecimal adjustForDownPayment(BigDecimal homePrice, BigDecimal downPayment) {
        BigDecimal ratio = downPayment.divide(homePrice, 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(new BigDecimal("0.20")) >= 0) {
            return new BigDecimal("-0.2000");
        }
        if (ratio.compareTo(new BigDecimal("0.10")) >= 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        if (ratio.compareTo(new BigDecimal("0.05")) >= 0) {
            return new BigDecimal("0.1800");
        }
        return new BigDecimal("0.3200");
    }

    private BigDecimal adjustForTerm(Integer termYears) {
        if (termYears <= 15) {
            return new BigDecimal("-0.5500");
        }
        if (termYears <= 20) {
            return new BigDecimal("-0.2000");
        }
        return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal adjustForZip(String zipCode) {
        int lastDigit = Character.getNumericValue(zipCode.charAt(zipCode.length() - 1));
        return BigDecimal.valueOf((lastDigit - 4) * 0.01).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal adjustForCreditScore(Integer creditScore) {
        if (creditScore >= 760) {
            return new BigDecimal("-0.3500");
        }
        if (creditScore >= 720) {
            return new BigDecimal("-0.1500");
        }
        if (creditScore >= 680) {
            return new BigDecimal("0.0500");
        }
        if (creditScore >= 640) {
            return new BigDecimal("0.2200");
        }
        return new BigDecimal("0.4500");
    }

    private BigDecimal adjustForDebtRatio(BigDecimal annualIncome, BigDecimal monthlyDebts) {
        BigDecimal monthlyIncome = annualIncome.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
        BigDecimal ratio = monthlyDebts.divide(monthlyIncome, 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(new BigDecimal("0.43")) > 0) {
            return new BigDecimal("0.2500");
        }
        if (ratio.compareTo(new BigDecimal("0.36")) > 0) {
            return new BigDecimal("0.1200");
        }
        if (ratio.compareTo(new BigDecimal("0.28")) < 0) {
            return new BigDecimal("-0.1000");
        }
        return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal adjustForBorrowerFlags(String loanProgram, Boolean firstTimeBuyer, Boolean vaEligible) {
        BigDecimal adjustment = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        if (Boolean.TRUE.equals(firstTimeBuyer)) {
            adjustment = adjustment.add(new BigDecimal("-0.0500"));
        }
        if ("VA".equals(normalize(loanProgram)) && Boolean.TRUE.equals(vaEligible)) {
            adjustment = adjustment.add(new BigDecimal("-0.1000"));
        }
        return adjustment;
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
