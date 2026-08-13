package com.deliveryflow.auth.config;

import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import com.deliveryflow.user.domain.UserRole;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile({"local", "docker"})
public class LocalAccountInitializer {
    @Bean
    CommandLineRunner createLocalAccounts(UserRepository repository, PasswordEncoder encoder,
            @Value("${app.bootstrap.admin-email:}") String adminEmail,
            @Value("${app.bootstrap.admin-password:}") String adminPassword,
            @Value("${app.bootstrap.driver-email:}") String driverEmail,
            @Value("${app.bootstrap.driver-password:}") String driverPassword) {
        return args -> {
            createIfConfigured(repository, encoder, adminEmail, adminPassword, "Local Administrator", UserRole.ADMIN);
            createIfConfigured(repository, encoder, driverEmail, driverPassword, "Local Driver", UserRole.DRIVER);
        };
    }

    private void createIfConfigured(UserRepository repository, PasswordEncoder encoder, String email, String password, String name, UserRole role) {
        if (email.isBlank() || password.isBlank() || repository.existsByEmail(email)) return;
        repository.save(new User(email, name, encoder.encode(password), role, true, LocalDateTime.now()));
    }
}

