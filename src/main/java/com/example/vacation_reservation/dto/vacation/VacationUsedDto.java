package com.example.vacation_reservation.dto.vacation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
public class VacationUsedDto {
    private String vacationTypeName; // 휴가 종류명
    private double usedDays;            // 사용 일수
    private LocalTime startTime;
    private LocalTime endTime;

    public VacationUsedDto(String vacationTypeName, double usedDays, LocalTime startTime, LocalTime endTime) {
        this.vacationTypeName = vacationTypeName;
        this.usedDays = usedDays;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

