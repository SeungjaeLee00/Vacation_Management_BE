package com.example.vacation_reservation.dto.vacation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsedVacationDto {
    private String vacationTypeName;
    private int usedDays;
}
