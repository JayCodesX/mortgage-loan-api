package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class InboundServiceTokenValidator {

    public void validate(String token, String secret, String issuer, String audience, String scope) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Missing service token");
        }

        SecretKey signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);

        Claims claims = claimsJws.getPayload();
        if (!issuer.equals(claims.getIssuer())) {
            throw new IllegalArgumentException("Invalid service token issuer");
        }
        if (claims.getAudience() == null || !claims.getAudience().contains(audience)) {
            throw new IllegalArgumentException("Invalid service token audience");
        }
        if (!scope.equals(claims.get("scope", String.class))) {
            throw new IllegalArgumentException("Invalid service token scope");
        }
        if (!"service".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("Invalid service token type");
        }
    }
}
