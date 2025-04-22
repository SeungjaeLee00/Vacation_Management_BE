package com.example.vacation_reservation.repository;

import com.example.vacation_reservation.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByHolidayDate(LocalDate date);
    List<Holiday> findByHolidayDateBetween(LocalDate startDate, LocalDate endDate);
}
