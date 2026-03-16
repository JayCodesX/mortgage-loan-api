package com.jaycodesx.mortgage.lead.service;

import java.util.UUID;

public record LeadJobMessage(
        int schemaVersion,
        String messageId,
        Long loanQuoteId,
        Long borrowerQuoteProfileId,
        String leadStatus,
        String leadSource,
        String serviceToken
) {
    public static final int SCHEMA_VERSION = 1;

    public LeadJobMessage withServiceToken(String token) {
        return new LeadJobMessage(
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
