package com.example.vacation_reservation.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user_department")
@Data
public class UserDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 사용자

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department; // 소속 부서

    private String position; // 직급
}
