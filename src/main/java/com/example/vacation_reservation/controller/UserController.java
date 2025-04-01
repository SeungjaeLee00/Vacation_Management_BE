// 회원가입(사용자 등록), 이메일 인증관련

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.ApiResponse;
import com.example.vacation_reservation.dto.PasswordCheckRequest;
import com.example.vacation_reservation.dto.UserRequestDto;
import com.example.vacation_reservation.security.JwtTokenProvider;
import com.example.vacation_reservation.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequestDto userRequestDto) {
        String message = userService.registerUser(userRequestDto);
        return ResponseEntity.ok(message);
    }

    // 인증번호 전송
    @PostMapping("/send-verification-code")
    public String sendVerificationCode(@RequestParam String email) {
        return userService.sendVerificationCode(email);
    }

    // 인증번호 확인
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = userService.verifyCode(email, code);
        if (isVerified) {
            return ResponseEntity.ok(new ApiResponse(true, "이메일 인증이 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "인증번호가 올바르지 않습니다."));
        }
    }


}
