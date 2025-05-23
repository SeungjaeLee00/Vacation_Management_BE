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
    private int level;  // 숫자가 낮을 수료ㅗㄱ 높은 직급 (신입사원 9 > .. > 대표이사 1)
}
