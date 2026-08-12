package com.deliveryflow.user.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    List<User> findByRoleAndActiveTrueOrderByNameAsc(UserRole role);
}
