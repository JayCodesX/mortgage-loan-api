package com.jaycodesx.mortgage.quote.service;

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

    public boolean hasSupportedSchemaVersion() {
        return schemaVersion == SCHEMA_VERSION;
    }
}
