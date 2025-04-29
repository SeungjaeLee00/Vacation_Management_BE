package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.vacation.VacationBalanceResponseDto;
import com.example.vacation_reservation.dto.vacation.VacationRequestDto;
import com.example.vacation_reservation.dto.vacation.VacationResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.repository.vacation.VacationTypeRepository;
import com.example.vacation_reservation.security.CustomUserDetails;
import com.example.vacation_reservation.service.vacation.VacationBalanceService;
import com.example.vacation_reservation.service.vacation.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vacations")
public class VacationController {

    @Autowired
    private VacationService vacationService;
    private final VacationTypeRepository vacationTypeRepository;
    private final VacationBalanceService vacationBalanceService;


    public VacationController(VacationService vacationService,
                              VacationTypeRepository vacationTypeRepository, VacationBalanceService vacationBalanceService) {
        this.vacationService = vacationService;
        this.vacationTypeRepository = vacationTypeRepository;
        this.vacationBalanceService = vacationBalanceService;
    }

    // 휴가 신청
    @PostMapping("/request")
    public String requestVacation(@RequestBody VacationRequestDto dto,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

//        System.out.println("Received startAt: " + dto.getStartAt());
//        System.out.println("Received endAt: " + dto.getEndAt());

        User user = userDetails.getUser();
        vacationService.requestVacation(user, dto);
        return "휴가 신청이 완료되었습니다!";
    }

    // 내가 신청한 휴가 목록 조회
    @GetMapping("/my-vacations")
    public ResponseEntity<List<VacationResponseDto>> getMyVacations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        List<VacationResponseDto> vacations = vacationService.getAllMyVacations(user.getEmployeeId());
        return ResponseEntity.ok(vacations);
    }

    // 내 잔여 휴가 조회
    @GetMapping("/balance")
    public ResponseEntity<List<VacationBalanceResponseDto>> getVacationBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        List<VacationBalanceResponseDto> balances = vacationBalanceService.getVacationBalances(user.getId(), LocalDate.now().getYear());
        return ResponseEntity.ok(balances);
    }
}

