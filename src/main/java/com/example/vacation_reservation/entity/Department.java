package com.example.vacation_reservation.entity;

import lombok.Data;

import javax.persistence.*;
//import java.util.List;

@Entity
@Table(name = "ojt_department")
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 부서명
}
