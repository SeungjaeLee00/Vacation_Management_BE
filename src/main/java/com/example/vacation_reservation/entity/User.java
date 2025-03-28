package com.example.vacation_reservation.entity;

import javax.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId; // 사번
    private String name;
    private String email;
    private String password;
    private boolean emailVerified; // 이메일 인증 여부

    // Constructor, Getter, Setter 생략

    // 기본 생성자 추가 (반드시 필요)
    public User() {
    }

    // Constructor
    public User(String employeeId, String name, String email, String password, boolean emailVerified) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
