package com.example.vacation_reservation.dto.vacation;

import com.example.vacation_reservation.validation.ValidTimeIfPartialDay;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@ValidTimeIfPartialDay
public class VacationUsedDto {
    @NotBlank(message = "휴가 종류명은 필수입니다.")
    private String vacationTypeName; // 휴가 종류명

    @Positive(message = "사용 일수는 0보다 커야 합니다.")
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

