package com.example.vacation_reservation.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ojt_Vacation_Type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String name;  // 연차, 하계휴가, 대체휴가, 포상휴가 등
}
