package com.example.vacation_reservation.dto.vacation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VacationResponseDto {

    private Long id;
    private String requestDate;
    private String status;
    private String reason;
    private String startAt;
    private String endAt;

    private List<VacationUsedDto> usedVacations;

    public VacationResponseDto(Long id, String requestDate, String status, String reason, String startAt, String endAt,  List<VacationUsedDto> usedVacations) {
        this.id = id;
        this.requestDate = requestDate;
        this.status = status;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
        this.usedVacations = usedVacations;
    }

    @Override
    public String toString() {
        return "VacationResponseDto{" +
                "id=" + id +
                ", requestDate='" + requestDate + '\'' +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", startAt='" + startAt + '\'' +
                ", endAt='" + endAt + '\'' +
                ", usedVacations='" + usedVacations + '\'' +
                '}';
    }
}
