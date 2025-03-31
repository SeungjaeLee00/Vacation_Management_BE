package com.example.vacation_reservation.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vacationType;  // 연차, 하계휴가, 대체휴가 등
    private String reason; // 휴가 사유

    private LocalDate requestDate = LocalDate.now();
    private String status = "Pending"; // 휴가 신청 상태 (기본 대기 상태)

    // @ElementCollection을 사용하여 List 데이터를 저장할 수 있음
    @ElementCollection
    @CollectionTable(name = "vacation_dates", joinColumns = @JoinColumn(name = "vacation_id"))
    @Column(name = "vacation_date")
    private List<String> vacationDates; // 휴가 날짜 (여러 날짜 선택 가능)

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVacationType() {
        return vacationType;
    }

    public void setVacationType(String vacationType) {
        this.vacationType = vacationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getVacationDates() {
        return vacationDates;
    }

    public void setVacationDates(List<String> vacationDates) {
        this.vacationDates = vacationDates;
    }
}
