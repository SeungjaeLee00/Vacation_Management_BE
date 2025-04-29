package com.example.vacation_reservation.repository.vacation;

import com.example.vacation_reservation.entity.vacation.VacationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VacationTypeRepository extends JpaRepository<VacationType, Long> {
    Optional<VacationType> findByName(String name);
}
