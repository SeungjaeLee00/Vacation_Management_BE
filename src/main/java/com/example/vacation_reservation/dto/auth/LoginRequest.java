package com.example.vacation_reservation.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "사번은 필수입니다.")
//    @Pattern 이건.. 사번 규칙 생기면 붙여야지
    private String employeeId; // 사번

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;   // 비밀번호
}
