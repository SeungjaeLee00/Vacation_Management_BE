package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.ApiResponse;
import com.example.vacation_reservation.dto.UserRequestDto;
import com.example.vacation_reservation.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequestDto userRequestDto) {
        String message = userService.registerUser(userRequestDto);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/send-verification-code")
    public String sendVerificationCode(@RequestParam String email) {
        return userService.sendVerificationCode(email);
    }

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
