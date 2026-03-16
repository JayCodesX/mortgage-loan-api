package com.jaycodesx.mortgage.borrower.controller;

import com.jaycodesx.mortgage.borrower.model.Borrower;
import com.jaycodesx.mortgage.borrower.service.BorrowerService;
import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BorrowerAdminControllerTest {

    @Test
    void returnsBorrowersForValidServiceToken() {
        BorrowerService borrowerService = mock(BorrowerService.class);
        ServiceTokenValidator validator = mock(ServiceTokenValidator.class);
        when(borrowerService.findAll()).thenReturn(List.of(new Borrower(1L, "Jay", "Coder", "jay@jaycodesx.dev", 741)));
        doNothing().when(validator).validateBorrowerReadToken("Bearer token");

        ResponseEntity<List<Borrower>> response = new BorrowerAdminController(borrowerService, validator).getBorrowers("Bearer token");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }
}
