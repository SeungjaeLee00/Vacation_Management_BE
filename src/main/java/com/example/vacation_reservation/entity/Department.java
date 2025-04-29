package com.example.vacation_reservation.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "department")
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 부서명

    @OneToOne
    @JoinColumn(name = "manager_id")
    private User manager; // 이 부서의 관리자

    @OneToMany(mappedBy = "department")
    private List<UserDepartment> userDepartments; // 이 부서에 소속된 사용자들
}

