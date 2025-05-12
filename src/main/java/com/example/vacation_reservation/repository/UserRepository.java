package com.example.vacation_reservation.repository;

import com.example.vacation_reservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);

    // User 객체를 가져올 때, Position 바로 로딩하지 말고 나중에 필요할 때(DB에서) 가져오셈
    @Query("SELECT u FROM User u JOIN FETCH u.position WHERE u.id = :id")
    Optional<User> findByIdWithPosition(Long id);
}
