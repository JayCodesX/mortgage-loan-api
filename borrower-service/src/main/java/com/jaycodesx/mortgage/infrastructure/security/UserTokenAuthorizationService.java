package com.jaycodesx.mortgage.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
@EnableConfigurationProperties(UserTokenProperties.class)
public class UserTokenAuthorizationService {

    private final UserTokenProperties properties;
    private final SecretKey signingKey;
    private final JwtDecoder oidcJwtDecoder;

    @Autowired
    public UserTokenAuthorizationService(UserTokenProperties properties) {
        this(properties, buildOidcJwtDecoder(properties));
    }

    UserTokenAuthorizationService(UserTokenProperties properties, JwtDecoder oidcJwtDecoder) {
        this.properties = properties;
        this.oidcJwtDecoder = oidcJwtDecoder;
        this.signingKey = usesInternalProvider(properties)
                ? Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8))
                : null;
    }

    public void requireAuthenticatedUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing bearer token");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (usesInternalProvider(properties)) {
            validateInternalToken(token);
            return;
        }

        validateOidcToken(token);
    }

    private void validateInternalToken(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);

        Claims claims = claimsJws.getPayload();
        if (!properties.issuer().equals(claims.getIssuer())) {
            throw new IllegalArgumentException("Invalid token issuer");
        }
        if (claims.getAudience() == null || !claims.getAudience().contains(properties.audience())) {
            throw new IllegalArgumentException("Invalid token audience");
        }
        if (!"user".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("Invalid token type");
        }
    }

    private void validateOidcToken(String token) {
        try {
            Jwt jwt = Objects.requireNonNull(oidcJwtDecoder, "OIDC JWT decoder must be configured").decode(token);
            if (!properties.issuer().equals(jwt.getIssuer() != null ? jwt.getIssuer().toString() : null)) {
                throw new IllegalArgumentException("Invalid OIDC token issuer");
            }

            List<String> audiences = jwt.getAudience();
            String authorizedParty = jwt.getClaimAsString("azp");
            String clientId = jwt.getClaimAsString("client_id");
            boolean audienceMatch = (audiences != null && audiences.contains(properties.audience()))
                    || properties.audience().equals(authorizedParty)
                    || properties.audience().equals(clientId);
            if (!audienceMatch) {
                throw new IllegalArgumentException("Invalid OIDC token audience");
            }
            if (jwt.getSubject() == null || jwt.getSubject().isBlank()) {
                throw new IllegalArgumentException("Invalid OIDC token subject");
            }
        } catch (JwtException ex) {
            throw new IllegalArgumentException("Invalid OIDC token", ex);
        }
    }

    private static boolean usesInternalProvider(UserTokenProperties properties) {
        return properties.provider() == null || properties.provider().isBlank()
                || "internal".equalsIgnoreCase(properties.provider());
    }

    private static JwtDecoder buildOidcJwtDecoder(UserTokenProperties properties) {
        if (usesInternalProvider(properties)) {
            return null;
        }
        if (properties.jwkSetUri() == null || properties.jwkSetUri().isBlank()) {
            throw new IllegalStateException("app.user-token.jwk-set-uri must be configured for OIDC mode");
        }
        return NimbusJwtDecoder.withJwkSetUri(properties.jwkSetUri()).build();
    }
}
