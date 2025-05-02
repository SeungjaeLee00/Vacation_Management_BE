package com.example.vacation_reservation.entity.vacation;

import com.example.vacation_reservation.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ojt_Vacation_Balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacationBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacation_type_id", nullable = false)
    private VacationType vacationType;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private double totalDays;

    private double usedDays = 0.0;

    private double remainingDays = 0.0;
}

// 휴가 부여 어떻게 할 건지 생각하셈: 스케쥴러...
