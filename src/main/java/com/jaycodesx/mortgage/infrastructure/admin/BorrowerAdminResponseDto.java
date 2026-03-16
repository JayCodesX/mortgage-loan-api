package com.jaycodesx.mortgage.infrastructure.admin;

public record BorrowerAdminResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        Integer creditScore
) {
}
