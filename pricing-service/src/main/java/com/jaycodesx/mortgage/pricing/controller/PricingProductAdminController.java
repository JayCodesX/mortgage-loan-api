package com.jaycodesx.mortgage.pricing.controller;

import com.jaycodesx.mortgage.infrastructure.security.ServiceTokenValidator;
import com.jaycodesx.mortgage.pricing.dto.PricingProductAdminRequestDto;
import com.jaycodesx.mortgage.pricing.dto.PricingProductAdminResponseDto;
import com.jaycodesx.mortgage.pricing.service.PricingProductAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/products")
public class PricingProductAdminController {

    private final PricingProductAdminService pricingProductAdminService;
    private final ServiceTokenValidator serviceTokenValidator;

    public PricingProductAdminController(
            PricingProductAdminService pricingProductAdminService,
            ServiceTokenValidator serviceTokenValidator
    ) {
        this.pricingProductAdminService = pricingProductAdminService;
        this.serviceTokenValidator = serviceTokenValidator;
    }

    @GetMapping
    public ResponseEntity<List<PricingProductAdminResponseDto>> getProducts(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        serviceTokenValidator.validatePricingTokenHeader(authorizationHeader);
        return ResponseEntity.ok(pricingProductAdminService.findAll());
    }

    @PostMapping
    public ResponseEntity<PricingProductAdminResponseDto> createProduct(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody PricingProductAdminRequestDto request
    ) {
        serviceTokenValidator.validatePricingTokenHeader(authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(pricingProductAdminService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PricingProductAdminResponseDto> updateProduct(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id,
            @RequestBody PricingProductAdminRequestDto request
    ) {
        serviceTokenValidator.validatePricingTokenHeader(authorizationHeader);
        return ResponseEntity.ok(pricingProductAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable Long id
    ) {
        serviceTokenValidator.validatePricingTokenHeader(authorizationHeader);
        pricingProductAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
