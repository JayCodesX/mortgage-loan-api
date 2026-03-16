package com.jaycodesx.mortgage.shared.controller;

import com.jaycodesx.mortgage.infrastructure.security.UserTokenAuthorizationService;
import com.jaycodesx.mortgage.shared.dto.AmortizationCalculationResponseDto;
import com.jaycodesx.mortgage.shared.dto.AmortizationResponseDto;
import com.jaycodesx.mortgage.shared.dto.LoanCreateRequestDto;
import com.jaycodesx.mortgage.shared.dto.MortgagePaymentResponseDto;
import com.jaycodesx.mortgage.shared.model.Loan;
import com.jaycodesx.mortgage.shared.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final UserTokenAuthorizationService userTokenAuthorizationService;

    public LoanController(LoanService loanService, UserTokenAuthorizationService userTokenAuthorizationService) {
        this.loanService = loanService;
        this.userTokenAuthorizationService = userTokenAuthorizationService;
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> createLoan(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody LoanCreateRequestDto request
    ) {
        try {
            userTokenAuthorizationService.requireAuthenticatedUser(authorizationHeader);
        } catch (IllegalArgumentException ex) {
            HttpStatus status = ex.getMessage().contains("token") ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST;
            return Mono.just(ResponseEntity.status(status).body((Object) ex.getMessage()));
        }

        return Mono.fromCallable(() -> (Object) loanService.createLoan(request))
                .subscribeOn(Schedulers.boundedElastic())
                .map(createdLoan -> ResponseEntity.status(HttpStatus.CREATED).body(createdLoan))
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.just(
                        ResponseEntity.badRequest().body((Object) ex.getMessage())
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Loan>> getLoanById(@PathVariable Long id) {
        return Mono.fromCallable(() -> loanService.findById(id)
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{id}/amortization")
    public Mono<ResponseEntity<Object>> getAmortization(@PathVariable Long id) {
        return Mono.fromCallable(() -> (Object) loanService.buildAmortization(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.just(ResponseEntity.notFound().build()));
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
