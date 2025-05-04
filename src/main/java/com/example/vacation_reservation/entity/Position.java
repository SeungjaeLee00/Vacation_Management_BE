package com.example.vacation_reservation.entity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "position")
@Data
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
