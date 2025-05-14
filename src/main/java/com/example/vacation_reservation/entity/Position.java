package com.example.vacation_reservation.entity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ojt_position")
@Data
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(nullable = false)
    private int level;  // 숫자가 클수록 높은 직급 (예: 사원 1 < 대리 2 < 과장 3 < 부장 4)
}
