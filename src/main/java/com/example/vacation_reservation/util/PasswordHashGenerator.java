// 비빈 암호화용 임시 코드

package com.example.vacation_reservation.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "jenny0417!";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("원본 비밀번호: " + rawPassword);
        System.out.println("암호화된 비밀번호: " + encodedPassword);
    }
}
