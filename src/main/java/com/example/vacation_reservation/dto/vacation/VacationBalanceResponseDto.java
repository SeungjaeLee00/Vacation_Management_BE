package com.example.vacation_reservation.dto.vacation;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class VacationBalanceResponseDto {

    @NotBlank(message = "휴가 종류명은 필수입니다.")
    private String vacationTypeName;   // 휴가 종류명 (예: 연차, 하계휴가)

    @Min(value = 0, message = "잔여 일수는 0 이상이어야 합니다.")
    private double remainingDays;      // 잔여 일수
}
