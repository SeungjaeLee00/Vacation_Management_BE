package com.example.vacation_reservation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacationResponseDto {

    private Long id;
    private String requestDate;
    private String usedVacationSummary;  // 휴가 종류별 사용 일수 합친 문자열
    private String status;
    private String reason;

    // 생성자
    public VacationResponseDto(Long id, String requestDate, String usedVacationSummary, String status, String reason) {
        this.id = id;
        this.requestDate = requestDate;
        this.usedVacationSummary = usedVacationSummary;
        this.status = status;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "VacationResponseDto{" +
                "id=" + id +
                ", requestDate='" + requestDate + '\'' +
                ", usedVacationSummary='" + usedVacationSummary + '\'' +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
