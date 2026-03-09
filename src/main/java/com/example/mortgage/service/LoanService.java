package com.example.mortgage.service;

import com.example.mortgage.dto.AmortizationCalculationResponseDto;
import com.example.mortgage.dto.AmortizationEntryDto;
import com.example.mortgage.dto.AmortizationResponseDto;
import com.example.mortgage.dto.LoanCreateRequestDto;
import com.example.mortgage.dto.MortgagePaymentResponseDto;
import com.example.mortgage.model.Loan;
import com.example.mortgage.repository.BorrowerRepository;
import com.example.mortgage.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BorrowerRepository borrowerRepository;

    public LoanService(LoanRepository loanRepository, BorrowerRepository borrowerRepository) {
        this.loanRepository = loanRepository;
        this.borrowerRepository = borrowerRepository;
    }

    public Loan createLoan(LoanCreateRequestDto request) {
        if (!borrowerRepository.existsById(request.borrowerId())) {
            throw new IllegalArgumentException("Borrower not found for id: " + request.borrowerId());
        }

        Loan loan = new Loan();
        loan.setBorrowerId(request.borrowerId());
        loan.setLoanAmount(request.loanAmount());
        loan.setInterestRate(request.interestRate());
        loan.setTermYears(request.termYears());
        loan.setStatus(request.status());

        return loanRepository.save(loan);
    }

    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    public AmortizationResponseDto buildAmortization(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found for id: " + loanId));

        int totalPayments = loan.getTermYears() * 12;
        BigDecimal monthlyRate = toMonthlyRate(loan.getInterestRate());

        BigDecimal monthlyPayment = calculateMonthlyPayment(loan.getLoanAmount(), monthlyRate, totalPayments);
        BigDecimal remainingBalance = loan.getLoanAmount();

        List<AmortizationEntryDto> schedule = new ArrayList<>();

        for (int paymentNumber = 1; paymentNumber <= totalPayments; paymentNumber++) {
            BigDecimal interestPaid = remainingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPaid = monthlyPayment.subtract(interestPaid).setScale(2, RoundingMode.HALF_UP);
            remainingBalance = remainingBalance.subtract(principalPaid).setScale(2, RoundingMode.HALF_UP);

            if (paymentNumber == totalPayments && remainingBalance.compareTo(BigDecimal.ZERO) != 0) {
                principalPaid = principalPaid.add(remainingBalance);
                remainingBalance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            }

            schedule.add(new AmortizationEntryDto(
                    paymentNumber,
                    monthlyPayment,
                    principalPaid,
                    interestPaid,
                    remainingBalance.max(BigDecimal.ZERO)
            ));
        }

        return new AmortizationResponseDto(
                loan.getId(),
                loan.getBorrowerId(),
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getTermYears(),
                monthlyPayment,
                schedule
        );
    }

    public AmortizationCalculationResponseDto calculateAmortizationPayment(
            BigDecimal principal,
            BigDecimal annualInterestRate,
            Integer termYears
    ) {
        validateCalculatorInputs(principal, annualInterestRate, termYears);

        int numberOfPayments = termYears * 12;
        BigDecimal monthlyInterestRate = toMonthlyRate(annualInterestRate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(principal, monthlyInterestRate, numberOfPayments);

        return new AmortizationCalculationResponseDto(
                principal.setScale(2, RoundingMode.HALF_UP),
                monthlyInterestRate.setScale(8, RoundingMode.HALF_UP),
                numberOfPayments,
                monthlyPayment
        );
    }

    public MortgagePaymentResponseDto calculateMortgagePayment(
            BigDecimal loanAmount,
            BigDecimal downPayment,
            BigDecimal annualInterestRate,
            Integer termYears
    ) {
        validateMortgageInputs(loanAmount, downPayment, annualInterestRate, termYears);

        BigDecimal financedPrincipal = loanAmount.subtract(downPayment);
        int numberOfPayments = termYears * 12;
        BigDecimal monthlyInterestRate = toMonthlyRate(annualInterestRate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(financedPrincipal, monthlyInterestRate, numberOfPayments);

        return new MortgagePaymentResponseDto(
                loanAmount.setScale(2, RoundingMode.HALF_UP),
                downPayment.setScale(2, RoundingMode.HALF_UP),
                financedPrincipal.setScale(2, RoundingMode.HALF_UP),
                annualInterestRate.setScale(4, RoundingMode.HALF_UP),
                termYears,
                numberOfPayments,
                monthlyInterestRate.setScale(8, RoundingMode.HALF_UP),
                monthlyPayment
        );
    }

    private void validateCalculatorInputs(BigDecimal principal, BigDecimal annualInterestRate, Integer termYears) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("principal must be greater than 0");
        }
        if (annualInterestRate == null || annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("annualInterestRate must be 0 or greater");
        }
        if (termYears == null || termYears <= 0) {
            throw new IllegalArgumentException("termYears must be greater than 0");
        }
    }

    private void validateMortgageInputs(
            BigDecimal loanAmount,
            BigDecimal downPayment,
            BigDecimal annualInterestRate,
            Integer termYears
    ) {
        if (loanAmount == null || loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("loanAmount must be greater than 0");
        }
        if (downPayment == null || downPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("downPayment must be 0 or greater");
        }
        if (downPayment.compareTo(loanAmount) >= 0) {
            throw new IllegalArgumentException("downPayment must be less than loanAmount");
        }
        if (annualInterestRate == null || annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("annualInterestRate must be 0 or greater");
        }
        if (termYears == null || termYears <= 0) {
            throw new IllegalArgumentException("termYears must be greater than 0");
        }
    }

    private BigDecimal toMonthlyRate(BigDecimal annualInterestRate) {
        return annualInterestRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyRate, int totalPayments) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(totalPayments), 2, RoundingMode.HALF_UP);
        }

        // M = P * r * (1+r)^n / ((1+r)^n - 1)
        double p = principal.doubleValue();
        double r = monthlyRate.doubleValue();
        double n = totalPayments;
        double factor = Math.pow(1 + r, n);
        double payment = (p * r * factor) / (factor - 1);

        return BigDecimal.valueOf(payment).setScale(2, RoundingMode.HALF_UP);
    }
}
