package com.deliveryflow.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.deliveryflow.user.api.CreateDriverRequest;
import com.deliveryflow.user.api.DriverResponse;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DriverServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final DriverService driverService = new DriverService(userRepository);

    @Test
    void createsActiveDriver() {
        when(userRepository.existsByEmail("driver1@deliveryflow.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DriverResponse response = driverService.create(new CreateDriverRequest("홍길동", "driver1@deliveryflow.com"));

        assertThat(response.name()).isEqualTo("홍길동");
        assertThat(response.role()).isEqualTo("DRIVER");
        assertThat(response.active()).isTrue();
    }

    @Test
    void rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("driver1@deliveryflow.com")).thenReturn(true);

        assertThatThrownBy(() -> driverService.create(new CreateDriverRequest("홍길동", "driver1@deliveryflow.com")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록된 이메일입니다.");
    }
}
