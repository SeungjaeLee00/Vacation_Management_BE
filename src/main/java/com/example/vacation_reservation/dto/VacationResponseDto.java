package com.example.vacation_reservation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class VacationResponseDto {

    private Long id;
    private LocalDate requestDate; // 휴가 신청 날짜
    private String vacationType; // 휴가 종류 (연차, 하계휴가 등)
    private String vacationDates; // 휴가 날짜 (여러 날짜 선택 가능)
    private String status; // 휴가 신청 상태 (예: 승인 대기, 승인, 거부 등)
    private String reason; // 휴가 사유

    // 생성자
    public VacationResponseDto(Long id, LocalDate requestDate, String vacationType, String vacationDates, String status, String reason) {
        this.id = id;
        this.requestDate = requestDate;
        this.vacationType = vacationType;
        this.vacationDates = vacationDates;
        this.status = status;
        this.reason = reason;
    }

//    public VacationResponseDto() {}
//
//    // Getter 메서드
//    public Long getId() {
//        return id;
//    }
//
//    public LocalDate getRequestDate() {
//        return requestDate;
//    }
//
//    public String getVacationType() {
//        return vacationType;
//    }
//
//    public String getVacationDates() {
//        return vacationDates;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public String getReason() {
//        return reason;
//    }
//
//    // Setter 메서드
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public void setRequestDate(LocalDate requestDate) {
//        this.requestDate = requestDate;
//    }
//
//    public void setVacationType(String vacationType) {
//        this.vacationType = vacationType;
//    }
//
//    public void setVacationDates(String vacationDates) {
//        this.vacationDates = vacationDates;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public void setReason(String reason) {
//        this.reason = reason;
//    }

    // toString(): 디버깅 용
    @Override
    public String toString() {
        return "VacationResponseDto{" +
                "id=" + id +
                ", requestDate=" + requestDate +
                ", vacationType='" + vacationType + '\'' +
                ", vacationDates=" + vacationDates +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
