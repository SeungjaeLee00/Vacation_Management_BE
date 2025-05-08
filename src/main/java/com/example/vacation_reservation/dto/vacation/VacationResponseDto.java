// 근데 응답용 DTO도 검증을 하나?...

package com.example.vacation_reservation.dto.vacation;

import com.example.vacation_reservation.entity.vacation.VacationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class VacationResponseDto {

    private Long id;
    private String requestDate;
    private String status;
    private String reason;
    private LocalDate startAt;
    private LocalDate endAt;
    private List<VacationUsedDto> usedVacations;

    public VacationResponseDto(Long id, String requestDate, VacationStatus status, String reason, LocalDate startAt, LocalDate endAt,  List<VacationUsedDto> usedVacations) {
        this.id = id;
        this.requestDate = requestDate;
        this.status = status.name();
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
        this.usedVacations = usedVacations;
    }
}
