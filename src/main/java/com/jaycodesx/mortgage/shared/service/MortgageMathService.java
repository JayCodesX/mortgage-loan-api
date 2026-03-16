package com.jaycodesx.mortgage.shared.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MortgageMathService {

    public BigDecimal toMonthlyRate(BigDecimal annualInterestRate) {
        return annualInterestRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal annualInterestRate, int totalPayments) {
        BigDecimal monthlyRate = toMonthlyRate(annualInterestRate);
        return calculateMonthlyPaymentForMonthlyRate(principal, monthlyRate, totalPayments);
    }

    public BigDecimal calculateMonthlyPaymentForMonthlyRate(BigDecimal principal, BigDecimal monthlyRate, int totalPayments) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(totalPayments), 2, RoundingMode.HALF_UP);
        }

        double p = principal.doubleValue();
        double r = monthlyRate.doubleValue();
        double n = totalPayments;
        double factor = Math.pow(1 + r, n);
        double payment = (p * r * factor) / (factor - 1);

        return BigDecimal.valueOf(payment).setScale(2, RoundingMode.HALF_UP);
    }
}
