package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.entity.Vacation;
import com.example.vacation_reservation.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacations")
public class VacationController {

    @Autowired
    private VacationService vacationService;

    // 휴가 신청 등록
    @PostMapping("/request")
    public String requestVacation(@RequestBody Vacation vacation) {
        vacationService.saveVacationRequest(vacation);
        return "휴가 신청이 완료되었습니다!";
    }
}

