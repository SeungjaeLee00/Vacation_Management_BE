// 인증 관련(로그인, 로그아웃, 내 정보 조회)

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.*;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.security.*;
import com.example.vacation_reservation.service.AuthService;
import com.example.vacation_reservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

//    // 내 정보 조회(로그인 후 접근 가능)
//    @GetMapping("/me")
//    public ResponseEntity<UserResponseDto> getMe(HttpServletRequest request) {
//        // HTTP 요청에서 JWT 토큰 추출 (JwtAuthenticationFilter에서 제공)
//        String token = JwtAuthenticationFilter.getJwtFromRequest(request);
//
//        if (token == null) {
//            return ResponseEntity.status(401).body(null); // 토큰이 없으면 401 Unauthorized 반환
//        }
//
//        // 현재 인증된 사용자 정보 가져오기
//        UserResponseDto userResponseDto = userService.getCurrentUser(token);
//
//        return ResponseEntity.ok(userResponseDto);
//    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        UserResponseDto dto = new UserResponseDto(user.getEmployeeId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(dto);
    }


// 비밀번호 확인
//    @PostMapping("/check-password")
//    public ResponseEntity<String> verifyPassword
//            (
//                    @AuthenticationPrincipal User user,
//            HttpServletRequest request,
//            @RequestBody PasswordCheckRequest passwordCheckRequest) {
//
//        String token = JwtAuthenticationFilter.getJwtFromRequest(request);
//
//        if (token == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 토큰이 없습니다.");
//        }
//
//        System.out.println("사용자가 입력한 비밀번호: " + passwordCheckRequest.getPassword()); // 사용자 입력 비밀번호
//        String rawPassword = passwordCheckRequest.getPassword(); // 사용자가 입력한 비밀번호
//
//        // 비밀번호 확인 로직 호출
//        boolean isPasswordValid = userService.verifyPassword(token, rawPassword);
//
//        if (isPasswordValid) {
//            return ResponseEntity.ok("비밀번호가 일치합니다.");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
//        }
//    }
    @PostMapping("/check-password")
    public ResponseEntity<String> verifyPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PasswordCheckRequest passwordCheckRequest) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증된 사용자가 아닙니다.");
        }

        String rawPassword = passwordCheckRequest.getPassword();  // 사용자 입력 비밀번호
        User user = userDetails.getUser();  // 실제 User 엔티티

        boolean isPasswordValid = userService.verifyPassword(user, rawPassword);

        if (isPasswordValid) {
            return ResponseEntity.ok("비밀번호가 일치합니다.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }
    }


    // 이름 바꾸기
//    @PutMapping("/update-name")
//    public ResponseEntity<String> updateUserName(
//            @RequestBody UserUpdateRequestDto requestDto,
//            HttpServletRequest request) {
//
//        // JWT 토큰 가져오기
//        String token = JwtAuthenticationFilter.getJwtFromRequest(request);
//        if (token == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
//        }
//
//        // 이름 변경 실행
//        boolean isUpdated = userService.updateUserName(token, requestDto.getNewName());
//
//        if(isUpdated) {
//            return ResponseEntity.ok("이름이 성공적으로 변경되었습니다.");
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이름 변경에 실패했습니다.");
//        }
//    }
    @PutMapping("/update-name")
    public ResponseEntity<String> changeName(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangeNameRequestDto dto) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증된 사용자가 아닙니다.");
        }

        userService.updateUserName(userDetails.getUser(), dto.getNewName());
        return ResponseEntity.ok("이름이 변경되었습니다.");
    }


    // 사용자 비밀번호 바꾸기
//    @PutMapping("/change-password")
//    public ResponseEntity<String> changePassword(
//            @RequestBody ChangePasswordRequestDto changePasswordRequestDto,
//            HttpServletRequest request) {
//
//        // JWT 토큰 가져오기
//        String token = JwtAuthenticationFilter.getJwtFromRequest(request);
//
//        if (token == null) {
//            return ResponseEntity.status(401).body("로그인이 필요합니다.");
//        }
//
//        try {
//            // 비밀번호 변경 처리
//            userService.changePassword(token, changePasswordRequestDto);
//            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(400).body(e.getMessage());
//        }
//    }
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangePasswordRequestDto dto) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증된 사용자가 아닙니다.");
        }

        try {
            userService.changePassword(userDetails.getUser(), dto);
            return ResponseEntity.ok("비밀번호가 변경되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

