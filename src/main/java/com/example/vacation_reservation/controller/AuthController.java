/**
 * 인증 관련 API를 제공하는 컨트롤러 클래스.
 * 로그인, 로그아웃, 내 정보 조회, 비밀번호 확인, 사용자 정보 변경 등의 기능을 제공
 */

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.ApiResponse;
import com.example.vacation_reservation.dto.auth.*;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.security.*;
import com.example.vacation_reservation.service.AuthService;
import com.example.vacation_reservation.service.user.UserService;
import com.example.vacation_reservation.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final EmailService emailService;

    /**
     * 로그인 요청 처리
     *
     * @param loginRequest 로그인 요청 DTO (사원번호, 비밀번호 포함)
     * @param response     응답 객체로 JWT 토큰을 쿠키로 설정
     * @return 로그인 성공 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Map<String, String> tokens = authService.login(loginRequest.getEmployeeId(), loginRequest.getPassword());

            // Access Token 쿠키 설정 (유효기간 15분)
            Cookie accessTokenCookie = new Cookie("accessToken", tokens.get("accessToken"));
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true); // HTTPS 환경에서만 전송 (배포환경 기준)
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(15 * 60); // 15분
            response.addCookie(accessTokenCookie);

            // Refresh Token 쿠키 설정 (유효기간 4시간)
            Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(4 * 60 * 60); // 4시간
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new ApiResponse(true, "로그인 성공"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "로그인 실패: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

            // Refresh Token 쿠키 삭제
            ResponseCookie deleteRefreshToken = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(0)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessToken.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString());

            return ResponseEntity.ok(new ApiResponse(true, "로그아웃 성공"));
        } catch (Exception e) {
            // 예외 처리 (GlobalExceptionHandler에서 처리하도록 던짐)
            throw new CustomException("로그아웃 실패: " + e.getMessage());
        }
    }

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

    /**
     * 현재 로그인한 사용자의 비밀번호가 일치하는지 확인
     *
     * @param userDetails           인증된 사용자 정보
     * @param passwordCheckRequest 사용자가 입력한 비밀번호
     * @return 비밀번호 일치 여부 메시지
     */
    @PostMapping("/check-password")
    public ResponseEntity<?> verifyPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PasswordCheckRequest passwordCheckRequest) {
        try {
            if (userDetails == null) {
                throw new CustomException("인증된 사용자가 아닙니다.");
            }
            String rawPassword = passwordCheckRequest.getPassword();  // 사용자 입력 비밀번호
            User user = userDetails.getUser();  // 실제 User 엔티티
            boolean isPasswordValid = userService.verifyPassword(user, rawPassword);

            if (isPasswordValid) {
                return ResponseEntity.ok("비밀번호가 일치합니다.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "비밀번호 확인 실패: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 API
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 쿠키에서 refreshToken 추출
            String refreshToken = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new CustomException("리프레시 토큰이 필요합니다.");
            }

            // AuthService를 통해 새 Access Token 발급
            String newAccessToken = authService.refreshAccessToken(refreshToken);

            // 새 Access Token을 쿠키에 담아서 응답 (HttpOnly, Secure 옵션 필수)
            Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(15 * 60); // 15분 유지, 필요에 따라 조절
            response.addCookie(accessTokenCookie);

            // 성공 응답 반환
            return ResponseEntity.ok(new ApiResponse(true, "Access Token 재발급 성공"));

        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "리프레시 토큰 처리 실패: " + e.getMessage()));
        }
    }

}

