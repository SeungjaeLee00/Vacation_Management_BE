package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.VacationResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.entity.Vacation;
import com.example.vacation_reservation.security.CustomUserDetails;
import com.example.vacation_reservation.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/vacations")
public class VacationController {

    @Autowired
    private VacationService vacationService;

    // 휴가 신청 등록
    @PostMapping("/request")
    public String requestVacation(@RequestBody Vacation vacation,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();          // 로그인 사용자 객체
        vacation.setUser(user);

        vacation.setRequestDate(LocalDate.now()); // 현재 날짜를 자동으로 설정
        vacation.setStatus("Pending");  // 기본 대기 상태

        vacationService.saveVacationRequest(vacation);
        return "휴가 신청이 완료되었습니다!";
    }

    // 내가 신청한 휴가 목록 조회
    @GetMapping("/my-vacations")
    public ResponseEntity<Page<VacationResponseDto>> getMyVacations(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String status) {

//        Page<VacationResponseDto> vacations = vacationService.getMyVacations(page, size, searchText, status);
        User user = userDetails.getUser();
        Page<VacationResponseDto> vacations = vacationService.getMyVacations(user.getEmployeeId(), page, size);
        return ResponseEntity.ok(vacations);
    }
}

