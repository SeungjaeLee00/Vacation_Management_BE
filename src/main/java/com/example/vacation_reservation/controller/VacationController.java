/**
 * 휴가 관련 API를 제공하는 컨트롤러 클래스
 * 휴가 신청, 내가 신청한 휴가 목록 조회, 잔여 휴가 조회 기능을 포함
 */

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.vacation.VacationBalanceResponseDto;
import com.example.vacation_reservation.dto.vacation.VacationRequestDto;
import com.example.vacation_reservation.dto.vacation.VacationResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.security.CustomUserDetails;
import com.example.vacation_reservation.service.vacation.VacationBalanceService;
import com.example.vacation_reservation.service.vacation.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vacations")
public class VacationController {

    private final VacationService vacationService;
    private final VacationBalanceService vacationBalanceService;

    /**
     * 사용자가 휴가를 신청
     * 사용자 정보와 휴가 신청 정보를 기반으로 휴가 신청을 처리
     *
     * @param dto 휴가 신청 데이터 전송 객체
     * @param userDetails 인증된 사용자 정보
     * @return 휴가 신청 완료 메시지
     */
    @PostMapping("/request")
    public String requestVacation(@RequestBody @Valid VacationRequestDto dto,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        vacationService.requestVacation(user, dto);
        return "휴가 신청이 완료되었습니다!";
    }

    /**
     * 사용자가 신청한 휴가 목록을 조회
     * 인증된 사용자의 휴가 목록을 반환
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 휴가 목록
     */
    @GetMapping("/my-vacations")
    public ResponseEntity<List<VacationResponseDto>> getMyVacations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        List<VacationResponseDto> vacations = vacationService.getAllMyVacations(user.getEmployeeId());
        return ResponseEntity.ok(vacations);
    }

    /**
     * 사용자의 잔여 휴가를 조회
     * 인증된 사용자의 잔여 휴가 목록을 반환
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자의 잔여 휴가 목록
     */
    @GetMapping("/balance")
    public ResponseEntity<List<VacationBalanceResponseDto>> getVacationBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        List<VacationBalanceResponseDto> balances = vacationBalanceService.getVacationBalances(user.getId(), LocalDate.now().getYear());
        return ResponseEntity.ok(balances);
    }
}

