package com.example.vacation_reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private final int CODE_LENGTH = 6;

    public String sendVerificationCode(String email) {
        // 랜덤 인증 코드 생성
        String code = generateRandomCode();

        // 이메일 내용 설정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드: " + code);

        // 이메일 발송
        mailSender.send(message);

        return code;
    }

    // 랜덤 인증 코드 생성
    private String generateRandomCode() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}