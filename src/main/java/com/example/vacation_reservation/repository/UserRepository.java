package com.example.vacation_reservation.repository;

import com.example.vacation_reservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
}
