package com.example.mortgage.controller;

import com.example.mortgage.dto.AmortizationCalculationResponseDto;
import com.example.mortgage.dto.AmortizationResponseDto;
import com.example.mortgage.dto.LoanCreateRequestDto;
import com.example.mortgage.dto.MortgagePaymentResponseDto;
import com.example.mortgage.model.Loan;
import com.example.mortgage.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public Mono<ResponseEntity<?>> createLoan(@Valid @RequestBody LoanCreateRequestDto request) {
        try {
            Loan createdLoan = loanService.createLoan(request);
            return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(createdLoan));
        } catch (IllegalArgumentException ex) {
            return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Loan>> getLoanById(@PathVariable Long id) {
        return Mono.just(
                loanService.findById(id)
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build())
        );
    }

    @GetMapping("/{id}/amortization")
    public Mono<ResponseEntity<?>> getAmortization(@PathVariable Long id) {
        try {
            AmortizationResponseDto amortization = loanService.buildAmortization(id);
            return Mono.just(ResponseEntity.ok(amortization));
        } catch (IllegalArgumentException ex) {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/amortization/calculate")
    public Mono<ResponseEntity<?>> calculateAmortization(
            @RequestParam BigDecimal principal,
            @RequestParam BigDecimal annualInterestRate,
            @RequestParam Integer termYears
    ) {
        try {
            AmortizationCalculationResponseDto result = loanService.calculateAmortizationPayment(
                    principal,
                    annualInterestRate,
                    termYears
            );
            return Mono.just(ResponseEntity.ok(result));
        } catch (IllegalArgumentException ex) {
            return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
        }
    }

    @GetMapping("/mortgage-payment/calculate")
    public Mono<ResponseEntity<?>> calculateMortgagePayment(
            @RequestParam BigDecimal loanAmount,
            @RequestParam BigDecimal downPayment,
            @RequestParam BigDecimal annualInterestRate,
            @RequestParam Integer termYears
    ) {
        try {
            MortgagePaymentResponseDto result = loanService.calculateMortgagePayment(
                    loanAmount,
                    downPayment,
                    annualInterestRate,
                    termYears
            );
            return Mono.just(ResponseEntity.ok(result));
        } catch (IllegalArgumentException ex) {
            return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
        }
    }
}
