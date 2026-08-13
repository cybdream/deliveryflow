package com.deliveryflow.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deliveryflow.order.domain.Order;
import com.deliveryflow.order.domain.OrderRepository;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import com.deliveryflow.user.domain.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthOrderIntegrationTest {
    private static final String ADMIN_EMAIL = "admin@test.local";
    private static final String ADMIN_PASSWORD = "TestPassword!123";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.save(new User(ADMIN_EMAIL, "Test Administrator", passwordEncoder.encode(ADMIN_PASSWORD),
                UserRole.ADMIN, true, LocalDateTime.now()));
        orderRepository.save(new Order("ORD-TEST-0001", "첫 번째 고객", "010-1111-1111", "서울시 중구 1",
                LocalDate.now(), LocalDateTime.now().minusMinutes(1)));
        orderRepository.save(new Order("ORD-TEST-0002", "두 번째 고객", "010-2222-2222", "서울시 중구 2",
                LocalDate.now(), LocalDateTime.now()));
    }

    @Test
    void loginIssuesAccessToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void orderListRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void administratorCanListOrdersWithCreatedAtSort() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", "Bearer " + login())
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].orderNo").value("ORD-TEST-0002"));
    }

    private String login() throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Matcher matcher = Pattern.compile("\\\"accessToken\\\":\\\"([^\\\"]+)\\\"").matcher(response);
        if (!matcher.find()) throw new IllegalStateException("Access token was not returned.");
        return matcher.group(1);
    }

    private String loginBody() {
        return "{\"email\":\"" + ADMIN_EMAIL + "\",\"password\":\"" + ADMIN_PASSWORD + "\"}";
    }
}

