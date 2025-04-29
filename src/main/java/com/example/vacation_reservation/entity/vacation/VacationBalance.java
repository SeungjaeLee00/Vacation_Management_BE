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
    private Double totalDays;

    private Double usedDays = 0.0;

    private Double remainingDays = 0.0;
}
