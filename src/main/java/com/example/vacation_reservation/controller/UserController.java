/**
 * 사용자 관련 API를 제공하는 컨트롤러 클래스
 * 비밀번호 찾기 기능을 포함
 */
package com.example.vacation_reservation.controller;
import com.example.vacation_reservation.dto.ApiResponse;
import com.example.vacation_reservation.dto.auth.ForgotPasswordRequest;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.service.email.EmailService;
import com.example.vacation_reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    /**
     * 사용자가 비밀번호를 잊어버렸을 때, 임시 비밀번호를 이메일로 발송
     * 사용자가 입력한 이메일로 임시 비밀번호를 생성하여 발송하며,
     * 해당 비밀번호를 사용자의 계정에 업데이트.
     *
     * @param request 비밀번호를 찾기 위한 이메일 요청 정보 (이메일)
     * @return 임시 비밀번호가 이메일로 발송되었다는 메시지
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        try {
            // 이메일로 사용자 찾기
            User user = userService.findUserByEmail(request.getEmail());

            // 임시 비밀번호 생성
            String temporaryPassword = generateRandomPassword();

            // 이메일로 임시 비밀번호 전송
            emailService.sendTemporaryPassword(user.getEmail(), temporaryPassword);

            // 유저 비밀번호를 임시 비밀번호로 업데이트
            userService.resetPassword(user.getEmail(), temporaryPassword, temporaryPassword);


            return ResponseEntity.ok(new ApiResponse(true, "임시 비밀번호가 이메일로 발송되었습니다."));
        } catch (CustomException e) {
            // 사용자 찾기 실패 예외 처리 (예: 이메일이 존재하지 않음)
            return new ResponseEntity<>(new ApiResponse(false, "이메일에 해당하는 사용자가 없습니다."), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "비밀번호 초기화 중 오류가 발생했습니다: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 랜덤한 임시 비밀번호를 생성하는 메서드 (비밀번호 규칙: 알파벳 소문자 + 숫자 + 특수문자, 최소 8자)
     *
     * @return 생성된 임시 비밀번호
     */
    private String generateRandomPassword() {
        try {
            int length = 10; // 비밀번호 길이, 최소 8자임
            String lowerCaseSet = "abcdefghijklmnopqrstuvwxyz";
            String digitSet = "0123456789";
            String specialSet = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

            // 모든 문자 집합을 결합하여 비밀번호 생성을 위한 문자 집합 구성
            String charSet = lowerCaseSet + digitSet + specialSet;

            StringBuilder password = new StringBuilder();
            Random random = new Random();

            // 최소 한 개의 소문자, 숫자, 특수문자를 포함하도록 보장
            password.append(lowerCaseSet.charAt(random.nextInt(lowerCaseSet.length())));
            password.append(digitSet.charAt(random.nextInt(digitSet.length())));
            password.append(specialSet.charAt(random.nextInt(specialSet.length())));

            // 나머지 문자는 임의로 선택하여 추가
            for (int i = 4; i < length; i++) {
                password.append(charSet.charAt(random.nextInt(charSet.length())));
            }

            // 비밀번호의 순서를 섞어서 반환
            return shuffleString(password.toString());
        } catch (Exception e) {
            throw new CustomException("임시 비밀번호 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 문자열의 순서를 섞는 메서드
     *
     * @param str 문자열
     * @return 섞인 문자열
     */
    private String shuffleString(String str) {
        StringBuilder shuffled = new StringBuilder(str);
        Random random = new Random();
        for (int i = 0; i < shuffled.length(); i++) {
            int j = random.nextInt(shuffled.length());
            char temp = shuffled.charAt(i);
            shuffled.setCharAt(i, shuffled.charAt(j));
            shuffled.setCharAt(j, temp);
        }
        return shuffled.toString();
    }
}
