/**
 * 인증 관련 API를 제공하는 컨트롤러 클래스.
 * 로그인, 로그아웃, 내 정보 조회, 비밀번호 확인, 사용자 정보 변경 등의 기능을 제공
 */

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.auth.LoginRequest;
import com.example.vacation_reservation.dto.auth.ChangeNameRequestDto;
import com.example.vacation_reservation.dto.auth.ChangePasswordRequestDto;
import com.example.vacation_reservation.dto.auth.PasswordCheckRequest;
import com.example.vacation_reservation.dto.user.UserResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.security.*;
import com.example.vacation_reservation.service.AuthService;
import com.example.vacation_reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 로그인 요청 처리
     *
     * @param loginRequest 로그인 요청 DTO (사원번호, 비밀번호 포함)
     * @param response     응답 객체로 JWT 토큰을 쿠키로 설정
     * @return 로그인 성공 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        // 로그인 서비스 호출
        Map<String, String> tokens = authService.login(loginRequest.getEmployeeId(), loginRequest.getPassword());

        // 쿠키에 JWT 토큰 설정 (Access Token)
        Cookie accessTokenCookie = new Cookie("accessToken", tokens.get("accessToken"));
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);    // HTTPS 환경에서만 쿠키 전송
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(900);    // 쿠키의 유효기간 설정 (15분)

        // 쿠키에 JWT 토큰 설정 (Refresh Token은 DB에 저장)
        response.addCookie(accessTokenCookie);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Login successful");

        return ResponseEntity.ok(responseBody);
    }

    /**
     * 로그아웃 요청 처리
     * 서버에서 Access Token 쿠키 삭제, Refresh Token null 처리
     * @param userDetails 인증된 사용자 정보
     * @param response
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        try {
            // 현재 로그인한 사용자 사번(employeeId) 추출
            String employeeId = userDetails.getUser().getEmployeeId();

            // Refresh Token 삭제 (User 테이블에서 null 처리)
            authService.clearRefreshToken(employeeId);

            // Access Token 쿠키 삭제
            ResponseCookie deleteAccessToken = ResponseCookie.from("accessToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(0)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessToken.toString());

            return ResponseEntity.ok("로그아웃 성공");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그아웃 실패: " + e.getMessage());
        }
    }

    /**
     * 현재 로그인된 사용자의 정보 조회
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 정보 DTO (사원번호, 이름, 이메일)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal @Valid CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        UserResponseDto dto = new UserResponseDto(user.getEmployeeId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(dto);
    }

    /**
     * 현재 로그인한 사용자의 비밀번호가 일치하는지 확인
     *
     * @param userDetails           인증된 사용자 정보
     * @param passwordCheckRequest 사용자가 입력한 비밀번호
     * @return 비밀번호 일치 여부 메시지
     */
    @PostMapping("/check-password")
    public ResponseEntity<String> verifyPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PasswordCheckRequest passwordCheckRequest) {

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

    /**
     * 현재 로그인한 사용자의 이름을 변경
     *
     * @param userDetails 인증된 사용자 정보
     * @param dto         변경할 이름 DTO
     * @return 이름 변경 성공 메시지
     */
    @PutMapping("/update-name")
    public ResponseEntity<String> changeName(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ChangeNameRequestDto dto) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증된 사용자가 아닙니다.");
        }

        userService.updateUserName(userDetails.getUser(), dto.getNewName());
        return ResponseEntity.ok("이름이 변경되었습니다.");
    }

    /**
     * 현재 로그인한 사용자의 비밀번호를 변경
     *
     * @param userDetails 인증된 사용자 정보
     * @param dto         비밀번호 변경 요청 DTO (현재 비밀번호, 새 비밀번호 포함)
     * @return 비밀번호 변경 성공 또는 실패 메시지
     */
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ChangePasswordRequestDto dto) {

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

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 API
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰이 필요합니다.");
        }

        try {
            // refresh token을 사용하여 새로운 access token을 발급
            String newAccessToken = authService.refreshAccessToken(refreshToken);

            // 새로운 access token 반환
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("accessToken", newAccessToken);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 유효하지 않거나 만료되었습니다.");
        }
    }
}

