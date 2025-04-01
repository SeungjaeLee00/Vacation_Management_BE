package com.example.vacation_reservation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestDto {
    private String currentPassword;  // 현재 비밀번호
    private String newPassword;      // 새로운 비밀번호
    private String confirmPassword;  // 새로운 비밀번호 확인
}
