package com.example.vacation_reservation.dto.user;

import com.example.vacation_reservation.entity.Position;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {

    private String employeeId;
    private String name;
    private String email;
    private String positionName;

    public UserResponseDto(String employeeId, String name, String email, String positionName) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.positionName = positionName;
    }
}
