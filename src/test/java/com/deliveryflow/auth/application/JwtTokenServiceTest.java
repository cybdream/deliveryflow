package com.deliveryflow.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class JwtTokenServiceTest {
    private final JwtTokenService tokenService = new JwtTokenService("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=", Duration.ofHours(1));

    @Test
    void createsTokenThatContainsUserIdentityAndRole() {
        String token = tokenService.createToken("admin@deliveryflow.local", "ADMIN");
        assertThat(tokenService.parse(token).getSubject()).isEqualTo("admin@deliveryflow.local");
        assertThat(tokenService.parse(token).get("role", String.class)).isEqualTo("ADMIN");
    }
}
