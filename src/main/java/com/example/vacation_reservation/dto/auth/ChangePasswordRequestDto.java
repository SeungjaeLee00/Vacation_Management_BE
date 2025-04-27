package com.example.vacation_reservation.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordRequestDto {
    private String currentPassword;  // 현재 비밀번호
    private String newPassword;      // 새로운 비밀번호
    private String confirmPassword;  // 새로운 비밀번호 확인
}
