package com.jaycodesx.mortgage.shared.controller;

import com.jaycodesx.mortgage.infrastructure.security.UserTokenAuthorizationService;
import com.jaycodesx.mortgage.shared.dto.AmortizationCalculationResponseDto;
import com.jaycodesx.mortgage.shared.dto.AmortizationResponseDto;
import com.jaycodesx.mortgage.shared.dto.LoanCreateRequestDto;
import com.jaycodesx.mortgage.shared.dto.MortgagePaymentResponseDto;
import com.jaycodesx.mortgage.shared.model.Loan;
import com.jaycodesx.mortgage.shared.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;
    @Mock
    private UserTokenAuthorizationService userTokenAuthorizationService;

    @InjectMocks
    private LoanController loanController;

    @Test
    void createLoanReturnsCreated() {
        Loan loan = new Loan(1L, 2L, new BigDecimal("300000.00"), new BigDecimal("6.1000"), 30, "PENDING");
        LoanCreateRequestDto request = new LoanCreateRequestDto(2L, new BigDecimal("300000.00"), new BigDecimal("6.1000"), 30, "PENDING");
        when(loanService.createLoan(request)).thenReturn(loan);

        ResponseEntity<?> response = loanController.createLoan("Bearer token", request).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(loan);
    }

    @Test
    void createLoanReturnsBadRequestOnValidationError() {
        LoanCreateRequestDto request = new LoanCreateRequestDto(2L, new BigDecimal("300000.00"), new BigDecimal("6.1000"), 30, "PENDING");
        when(loanService.createLoan(request)).thenThrow(new IllegalArgumentException("Borrower not found"));

        ResponseEntity<?> response = loanController.createLoan("Bearer token", request).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Borrower not found");
    }

    @Test
    void getLoanByIdReturnsFoundOrNotFound() {
        Loan loan = new Loan(1L, 2L, new BigDecimal("300000.00"), new BigDecimal("6.1000"), 30, "PENDING");
        when(loanService.findById(1L)).thenReturn(Optional.of(loan));
        when(loanService.findById(99L)).thenReturn(Optional.empty());

        assertThat(loanController.getLoanById(1L).block().getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loanController.getLoanById(99L).block().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAmortizationHandlesMissingLoan() {
        AmortizationResponseDto dto = new AmortizationResponseDto(1L, 2L, BigDecimal.ONE, BigDecimal.ONE, 30, BigDecimal.ONE, List.of());
        when(loanService.buildAmortization(1L)).thenReturn(dto);
        when(loanService.buildAmortization(9L)).thenThrow(new IllegalArgumentException("Loan not found"));

        assertThat(loanController.getAmortization(1L).block().getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loanController.getAmortization(9L).block().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void calculateEndpointsReturnSuccessAndBadRequest() {
        when(loanService.calculateAmortizationPayment(BigDecimal.ONE, BigDecimal.TEN, 30))
                .thenReturn(new AmortizationCalculationResponseDto(BigDecimal.ONE, BigDecimal.ONE, 360, BigDecimal.ONE));
        when(loanService.calculateMortgagePayment(BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN, 30))
                .thenReturn(new MortgagePaymentResponseDto(BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, 30, 360, BigDecimal.ONE, BigDecimal.ONE));

        assertThat(loanController.calculateAmortization(BigDecimal.ONE, BigDecimal.TEN, 30).block().getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loanController.calculateMortgagePayment(BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN, 30).block().getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
