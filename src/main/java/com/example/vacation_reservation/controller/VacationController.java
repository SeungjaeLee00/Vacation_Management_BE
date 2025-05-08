/**
 * 휴가 관련 API를 제공하는 컨트롤러 클래스
 * 휴가 신청, 내가 신청한 휴가 목록 조회, 잔여 휴가 조회 기능을 포함
 */

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.ApiResponse;
import com.example.vacation_reservation.dto.vacation.VacationBalanceResponseDto;
import com.example.vacation_reservation.dto.vacation.VacationInfoDto;
import com.example.vacation_reservation.dto.vacation.VacationRequestDto;
import com.example.vacation_reservation.dto.vacation.VacationResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.security.CustomUserDetails;
import com.example.vacation_reservation.service.vacation.VacationBalanceService;
import com.example.vacation_reservation.service.vacation.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> requestVacation(@RequestBody @Valid VacationRequestDto dto,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            vacationService.requestVacation(user, dto);

            return ResponseEntity.ok("휴가 신청이 완료되었습니다!");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "휴가 신청 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }


    /**
     * 사용자가 신청한 휴가 목록을 조회
     * 인증된 사용자의 휴가 목록을 반환
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 휴가 목록
     */
    @GetMapping("/my-vacations")
    public ResponseEntity<?> getMyVacations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();

            List<VacationResponseDto> vacations = vacationService.getAllMyVacations(user.getEmployeeId());

            return ResponseEntity.ok(vacations);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "휴가 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }


    /**
     * 사용자의 잔여 휴가를 조회
     * 인증된 사용자의 잔여 휴가 목록을 반환
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자의 잔여 휴가 목록
     */
    @GetMapping("/balance")
    public ResponseEntity<?> getVacationBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            List<VacationBalanceResponseDto> balances = vacationBalanceService.getVacationBalances(user.getId(), LocalDate.now().getYear());

            return ResponseEntity.ok(balances);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "잔여 휴가 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }


    /**
     * 대기 중인 휴가를 취소하는 API.
     *
     * @param vacationId 휴가 ID
     * @return 취소 완료 메시지
     */
    @DeleteMapping("/{vacationId}/cancel")
    public ResponseEntity<ApiResponse> cancelPendingVacation(@PathVariable Long vacationId) {
        try {
            vacationService.cancelVacation(vacationId);
            return ResponseEntity.ok(new ApiResponse(true, "휴가 신청이 취소되었습니다."));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            // 다른 예외가 발생한 경우
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "휴가 취소 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 취소 상태인 휴가 삭제
     *
     * @param vacationId 휴가 ID
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{vacationId}/delete")
    public ResponseEntity<ApiResponse> deleteVacation(@PathVariable Long vacationId) {
        try {
            vacationService.deleteVacation(vacationId);
            return ResponseEntity.ok(new ApiResponse(true, "휴가 신청이 삭제되었습니다."));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "휴가 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }


    /**
     * 내 부서 사용자들의 휴가 목록을 조회.
     *
     * 로그인한 사용자의 부서를 기준으로, 해당 부서에 속한 모든 직원의 휴가 정보를 조회.
     * 휴가 정보는 MyBatis를 통해 가져오며, 사용자 이름, 직급, 휴가 종류, 시작일, 종료일을 포함.
     *
     * @param userDetails 인증된 사용자 정보
     * @return 부서 구성원의 휴가 목록 (List<VacationInfoDto>) 또는 오류 응답
     */
    @GetMapping("/my-department")
    public ResponseEntity<?> getMyDepartmentVacations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<VacationInfoDto> list = vacationService.getVacationsInMyDepartment(userDetails.getUser());

            // 부서 내에 휴가자가 없다면 빈 리스트 반환
            if (list.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(true, "부서 내 휴가자 없음", list));
            }

            return ResponseEntity.ok(new ApiResponse(true, "부서 휴가 목록 조회 성공", list));
        } catch (CustomException e) {
            // 사용자 관련 예외 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            // 일반 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "부서 휴가 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}

