package com.example.vacation_reservation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor  // 이게 모든 필드를 다 받는 생성자를 자동으로 만들어줌
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId; // 사번
    private String name;
    private String email;
    private String password;
    private boolean emailVerified; // 이메일 인증 여부

    // id 없이 사용하는 생성자 따로 추가해줘야 함! 생성할 User 객체에서 id 안쓸거니깐~
    public User(String employeeId, String name, String email, String password, boolean emailVerified) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
    }
}