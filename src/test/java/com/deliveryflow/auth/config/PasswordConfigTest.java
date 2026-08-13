package com.deliveryflow.auth.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void encodesAndVerifiesPasswordWithoutKeepingPlainText() {
        String password = "deliveryflow-password";
        String hash = encoder.encode(password);
        assertThat(hash).isNotEqualTo(password);
        assertThat(encoder.matches(password, hash)).isTrue();
    }
}
