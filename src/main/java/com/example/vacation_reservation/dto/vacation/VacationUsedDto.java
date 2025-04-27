package com.example.vacation_reservation.dto.vacation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacationUsedDto {
    private String vacationTypeName; // 휴가 종류명
    private int usedDays;            // 사용 일수

    public VacationUsedDto(String vacationTypeName, int usedDays) {
        this.vacationTypeName = vacationTypeName;
        this.usedDays = usedDays;
    }
}

