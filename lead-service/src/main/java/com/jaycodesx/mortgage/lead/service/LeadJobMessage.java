package com.jaycodesx.mortgage.lead.service;

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

    public boolean hasSupportedSchemaVersion() {
        return schemaVersion == SCHEMA_VERSION;
    }
}
