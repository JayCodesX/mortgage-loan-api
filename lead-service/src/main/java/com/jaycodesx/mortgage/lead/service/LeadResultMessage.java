package com.jaycodesx.mortgage.lead.service;

import java.util.UUID;

public record LeadResultMessage(
        int schemaVersion,
        String messageId,
        Long loanQuoteId,
        Long borrowerQuoteProfileId,
        String leadStatus,
        String leadSource,
        String serviceToken
) {
    public static final int SCHEMA_VERSION = 1;

    public LeadResultMessage withServiceToken(String token) {
        return new LeadResultMessage(
                schemaVersion,
                messageId != null ? messageId : UUID.randomUUID().toString(),
                loanQuoteId,
                borrowerQuoteProfileId,
                leadStatus,
                leadSource,
                token
        );
    }

    public boolean hasSupportedSchemaVersion() {
        return schemaVersion == SCHEMA_VERSION;
    }
}
