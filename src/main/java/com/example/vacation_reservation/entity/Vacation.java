package com.example.vacation_reservation.entity;

import lombok.Data;  
// @Data 어노테이션을 사용하면 Lombok 라이브러리가 자동으로 getter, setter, toString, equals, hashCode 같은 메서드를 생성해줌

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vacationType;  // 연차, 하계휴가, 대체휴가 등
    private String reason; // 휴가 사유

    private LocalDate requestDate = LocalDate.now();
    private String status = "Pending"; // 휴가 신청 상태 (기본 대기 상태)

    @ElementCollection
    @CollectionTable(name = "vacation_dates", joinColumns = @JoinColumn(name = "vacation_id"))
    @Column(name = "vacation_date")
    private List<String> vacationDates; // 휴가 날짜 (여러 날짜 선택 가능)
}
