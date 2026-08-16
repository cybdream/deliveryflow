package com.deliveryflow.auth.application;

import com.deliveryflow.auth.api.LoginRequest;
import com.deliveryflow.common.api.ApiException;
import com.deliveryflow.auth.api.LoginResponse;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(User::isActive)
                .filter(found -> found.getPasswordHash() != null && passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(() -> ApiException.unauthorized("error.auth.invalidCredentials"));
        String role = user.getRole().name();
        return new LoginResponse(jwtTokenService.createToken(user.getEmail(), role), "Bearer",
                jwtTokenService.expiresInSeconds(), role, user.getName());
    }
}
