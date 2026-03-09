package com.example.mortgage.controller;

import com.example.mortgage.dto.BorrowerCreateRequestDto;
import com.example.mortgage.model.Borrower;
import com.example.mortgage.service.BorrowerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping
    public Mono<ResponseEntity<Borrower>> createBorrower(@Valid @RequestBody BorrowerCreateRequestDto request) {
        Borrower createdBorrower = borrowerService.createBorrower(request);
        return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(createdBorrower));
    }
}
