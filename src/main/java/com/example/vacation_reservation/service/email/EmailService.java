/**
 * 이메일 전송 관련 기능을 제공하는 서비스 클래스
 * <p>
 * 임시 비밀번호 전송 기능과 내부용 랜덤 인증 코드 생성 기능을 제공
 */
package com.example.vacation_reservation.service.email;

import com.example.vacation_reservation.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 지정된 이메일 주소로 임시 비밀번호를 전송
     *
     * @param email        수신자 이메일 주소
     * @param tempPassword 전송할 임시 비밀번호
     */
    public void sendTemporaryPassword(String email, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("임시 비밀번호 안내");
            message.setText("임시 비밀번호는 " + tempPassword + " 입니다.\n로그인 후 꼭 비밀번호를 변경해 주세요.");


            mailSender.send(message);

        } catch (Exception e) {
            throw new CustomException("이메일 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}