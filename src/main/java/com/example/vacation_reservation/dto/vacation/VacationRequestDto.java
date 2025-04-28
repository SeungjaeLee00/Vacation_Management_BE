package com.example.vacation_reservation.dto.vacation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class VacationRequestDto {
    private LocalDate startAt;
    private LocalDate endAt;
    private String reason;
    private List<VacationUsedDto> usedVacations;
}

