package com.jaycodesx.mortgage.lead.dto;

public record MortgageLeadResponseDto(
        Long id,
        Long loanQuoteId,
        String leadStatus,
        String leadSource
) {
}
