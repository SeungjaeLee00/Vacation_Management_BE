package com.example.vacation_reservation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {

    private String employeeId;
    private String name;
    private String email;

    // 생성자
    public UserResponseDto(String employeeId, String name, String email) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
    }
}
