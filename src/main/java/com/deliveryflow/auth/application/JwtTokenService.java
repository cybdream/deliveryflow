package com.deliveryflow.auth.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtTokenService(@Value("${app.jwt.secret}") String secret,
                           @Value("${app.jwt.expiration:PT1H}") Duration expiration) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    public String createToken(String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder().subject(email).claim("role", role).issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration))).signWith(signingKey).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    public long expiresInSeconds() { return expiration.toSeconds(); }
}
