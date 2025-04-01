package com.example.vacation_reservation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequestDto {
    private String newName; // 변경할 이름
}
