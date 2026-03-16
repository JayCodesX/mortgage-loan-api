package com.jaycodesx.mortgage.borrower.controller;

import com.jaycodesx.mortgage.borrower.model.Borrower;
import com.jaycodesx.mortgage.borrower.service.BorrowerService;
import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/borrowers")
public class BorrowerAdminController {

    private final BorrowerService borrowerService;
    private final ServiceTokenValidator serviceTokenValidator;

    public BorrowerAdminController(BorrowerService borrowerService, ServiceTokenValidator serviceTokenValidator) {
        this.borrowerService = borrowerService;
        this.serviceTokenValidator = serviceTokenValidator;
    }

    @GetMapping
    public ResponseEntity<List<Borrower>> getBorrowers(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        serviceTokenValidator.validateBorrowerReadToken(authorizationHeader);
        return ResponseEntity.ok(borrowerService.findAll());
    }
}
