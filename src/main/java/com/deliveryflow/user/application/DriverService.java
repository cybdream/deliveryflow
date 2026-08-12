package com.deliveryflow.user.application;

import com.deliveryflow.user.api.CreateDriverRequest;
import com.deliveryflow.user.api.DriverResponse;
import com.deliveryflow.user.domain.User;
import com.deliveryflow.user.domain.UserRepository;
import com.deliveryflow.user.domain.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DriverService {

    private final UserRepository userRepository;

    public DriverService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public DriverResponse create(CreateDriverRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        User driver = new User(request.email(), request.name(), UserRole.DRIVER, true, LocalDateTime.now());
        return DriverResponse.from(userRepository.save(driver));
    }

    public List<DriverResponse> findAllActive() {
        return userRepository.findByRoleAndActiveTrueOrderByNameAsc(UserRole.DRIVER).stream()
                .map(DriverResponse::from)
                .toList();
    }
}
