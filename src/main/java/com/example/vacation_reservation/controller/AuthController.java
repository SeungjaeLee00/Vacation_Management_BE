// 인증 관련(로그인, 로그아웃, 내 정보 조회)

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.LoginRequest;
import com.example.vacation_reservation.dto.UserResponseDto;
import com.example.vacation_reservation.security.JwtTokenProvider;
import com.example.vacation_reservation.service.AuthService;
import com.example.vacation_reservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 로그인 서비스 호출
        String token = authService.login(loginRequest.getEmployeeId(), loginRequest.getPassword());
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok().body("로그아웃 성공");
    }

    // 내 정보 조회(로그인 후 접근 가능)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe() {
        // 현재 인증된 사용자 정보 가져오기
        UserResponseDto userResponseDto = userService.getCurrentUser();
        return ResponseEntity.ok(userResponseDto);
    }
}
