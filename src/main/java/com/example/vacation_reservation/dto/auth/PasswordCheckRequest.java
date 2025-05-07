package com.example.vacation_reservation.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class PasswordCheckRequest {

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

//    // 필드 초기화 용
//    public PasswordCheckRequest(String password) {
//        this.password = password;
//    }
//
//    // toString(): 디버깅 용
//    @Override
//    public String toString() {
//        return "PasswordCheckRequest{" +
//                "password='" + password + '\'' +
//                '}';
//    }
}
