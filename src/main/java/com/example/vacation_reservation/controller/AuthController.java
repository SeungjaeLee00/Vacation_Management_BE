// 인증 관련(로그인, 로그아웃, 내 정보 조회)

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.LoginRequest;
import com.example.vacation_reservation.dto.UserResponseDto;
import com.example.vacation_reservation.security.*;
import com.example.vacation_reservation.service.AuthService;
import com.example.vacation_reservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // 로그인 서비스 호출
        String token = authService.login(loginRequest.getEmployeeId(), loginRequest.getPassword());

        // 쿠키에 JWT 토큰 설정
        Cookie cookie = new Cookie("Token", token);
        cookie.setHttpOnly(false);
        cookie.setSecure(true);    // HTTPS 환경에서만 쿠키 전송
        cookie.setPath("/");       // 애플리케이션의 모든 경로에서 쿠키를 사용할 수 있도록 설정
        cookie.setMaxAge(14400);    // 쿠키의 유효기간 설정 (4시간)

        response.addCookie(cookie);

        return ResponseEntity.ok().body("로그인 성공");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok().body("로그아웃 성공");
    }

    // 내 정보 조회(로그인 후 접근 가능)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe(HttpServletRequest request) {
        // HTTP 요청에서 JWT 토큰 추출 (JwtAuthenticationFilter에서 제공)
        String token = JwtAuthenticationFilter.getJwtFromRequest(request);

        if (token == null) {
            return ResponseEntity.status(401).body(null); // 토큰이 없으면 401 Unauthorized 반환
        }

        // 현재 인증된 사용자 정보 가져오기
        UserResponseDto userResponseDto = userService.getCurrentUser(token);

        return ResponseEntity.ok(userResponseDto);
    }
}

