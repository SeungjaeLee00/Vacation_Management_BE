package com.example.vacation_reservation.dto.vacation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VacationBalanceResponseDto {
    private String vacationTypeName;   // 휴가 종류명 (예: 연차, 하계휴가)
    private double remainingDays;      // 잔여 일수
}
