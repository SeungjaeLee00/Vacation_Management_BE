package com.example.vacation_reservation.dto;

public class LoginRequest {
    private String employeeId; // 사번
    private String password;   // 비밀번호

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
