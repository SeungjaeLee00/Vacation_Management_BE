package com.example.vacation_reservation.repository.vacation;
import com.example.vacation_reservation.entity.vacation.VacationBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VacationBalanceRepository extends JpaRepository<VacationBalance, Long> {

    // 특정 유저, 연도에 대한 모든 휴가 잔여 조회
    List<VacationBalance> findByUserIdAndYear(Long userId, Integer year);

    // 특정 유저, 연도, 휴가 타입별 잔여 조회
    Optional<VacationBalance> findByUserIdAndVacationTypeIdAndYear(Long userId, Long vacationTypeId, Integer year);

}
