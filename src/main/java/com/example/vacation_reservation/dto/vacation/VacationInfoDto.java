// 조회용이니간 유효성 검사 안넣음~

package com.example.vacation_reservation.dto.vacation;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VacationInfoDto {
    private Long vacationId;
    private String userName;
    private String positionName;
    private String vacationType;
    private LocalDate start_at;
    private LocalDate end_at;
}
