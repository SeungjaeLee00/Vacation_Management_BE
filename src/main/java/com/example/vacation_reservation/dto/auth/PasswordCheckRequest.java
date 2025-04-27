package com.example.vacation_reservation.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordCheckRequest {
    private String password;

    // 생성자: 필드 초기화 용
    public PasswordCheckRequest(String password) {
        this.password = password;
    }

    // toString(): 디버깅 용
    @Override
    public String toString() {
        return "PasswordCheckRequest{" +
                "password='" + password + '\'' +
                '}';
    }
}
