package com.example.vacation_reservation.entity.vacation;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "ojt_Vacation_Used")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacationUsed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 휴가 신청에 속하는지 (Many VacationUsed -> One Vacation)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacation_id", nullable = false)
    private Vacation vacation;

    // 어떤 휴가 종류인지 (Many VacationUsed -> One VacationType)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacation_type_id", nullable = false)
    private VacationType vacationType;

    // 해당 휴가 종류로 사용한 일수
    @Column(scale = 2, nullable = false)
    private double usedDays;

    // 휴가 갯수 1개 미만일 떄 휴가 시작 시간
    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;

    // 휴가 갯수 1개 미만일 떄 휴가 끝나는 시간
    @Column(name = "end_time", nullable = true)
    private LocalTime endTime;
}
