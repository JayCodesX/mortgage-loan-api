package com.jaycodesx.mortgage.quote.service;

import com.jaycodesx.mortgage.quote.dto.PublicLoanQuoteRequestDto;
import com.jaycodesx.mortgage.quote.dto.QuoteRefinementRequestDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuoteSessionService {

    private static final Duration DEDUPE_TTL = Duration.ofMinutes(10);
    private static final Duration STATUS_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;

    public QuoteSessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String resolveSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return sessionId.trim();
    }

    public String fingerprintPublicQuote(String sessionId, PublicLoanQuoteRequestDto request) {
        return hash(String.join("|",
                "public",
                sessionId,
                request.homePrice().toPlainString(),
                request.downPayment().toPlainString(),
                normalize(request.zipCode()),
                normalize(request.loanProgram()),
                normalize(request.propertyUse()),
                String.valueOf(request.termYears())
        ));
    }

    public String fingerprintRefinedQuote(String sessionId, Long quoteId, QuoteRefinementRequestDto request) {
        return hash(String.join("|",
                "refine",
                sessionId,
                String.valueOf(quoteId),
                normalize(request.email()),
                String.valueOf(request.creditScore()),
                request.annualIncome().toPlainString(),
                request.monthlyDebts().toPlainString(),
                request.cashReserves().toPlainString(),
                String.valueOf(request.firstTimeBuyer()),
                String.valueOf(request.vaEligible())
        ));
    }

    public Optional<Long> findQuoteId(String fingerprint) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String rawValue = ops.get(fingerprintKey(fingerprint));
        if (rawValue == null || rawValue.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(rawValue));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public void rememberQuote(String fingerprint, Long quoteId, String processingStatus) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(fingerprintKey(fingerprint), String.valueOf(quoteId), DEDUPE_TTL);
        ops.set(statusKey(quoteId), processingStatus, STATUS_TTL);
    }

    public void cacheQuoteStatus(Long quoteId, String processingStatus) {
        redisTemplate.opsForValue().set(statusKey(quoteId), processingStatus, STATUS_TTL);
    }

    private String fingerprintKey(String fingerprint) {
        return "quote:fingerprint:" + fingerprint;
    }

    private String statusKey(Long quoteId) {
        return "quote:status:" + quoteId;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private String hash(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}
