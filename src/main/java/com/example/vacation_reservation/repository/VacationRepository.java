package com.example.vacation_reservation.repository;

import com.example.vacation_reservation.entity.Vacation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRepository extends JpaRepository<Vacation, Long> {

//    // 상태로 필터링
//    Page<Vacation> findByStatus(String status, Pageable pageable);
//
//    // 상태 + 검색어(휴가 유형 또는 사유) 필터링
//    Page<Vacation> findByStatusAndVacationTypeOrStatusAndReasonContaining(
//            String status1, String vacationType, String status2, String reason, Pageable pageable
//    );

    // 모든 휴가 목록 조회
    Page<Vacation> findAll(Pageable pageable);
}
