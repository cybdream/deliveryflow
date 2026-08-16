package com.deliveryflow.auth.config;

import com.deliveryflow.auth.application.JwtAuthenticationFilter;
import com.deliveryflow.common.api.ApiAccessDeniedHandler;
import com.deliveryflow.common.api.ApiAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter,
            ApiAuthenticationEntryPoint authenticationEntryPoint, ApiAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/admin.html", "/api/v1/auth/login", "/api/v1/health", "/api/v1/tracking/**", "/error", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/drivers/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/dashboard/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/deliveries").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/deliveries").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/deliveries/me").hasRole("DRIVER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/deliveries/*/status").hasAnyRole("ADMIN", "DRIVER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/deliveries/*/histories").hasAnyRole("ADMIN", "DRIVER")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
