// 회원가입(사용자 등록), 이메일 인증관련

//import com.example.vacation_reservation.dto.ApiResponse;
//import com.example.vacation_reservation.dto.UserRequestDto;
//import com.example.vacation_reservation.service.UserService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    private final UserService userService;
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    // 회원가입
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody UserRequestDto userRequestDto) {
//        String message = userService.registerUser(userRequestDto);
//        return ResponseEntity.ok(message);
//    }
//
//    // 인증번호 전송
//    @PostMapping("/send-verification-code")
//    public String sendVerificationCode(@RequestParam String email) {
//        return userService.sendVerificationCode(email);
//    }
//
//    // 인증번호 확인
//    @PostMapping("/verify-code")
//    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
//        boolean isVerified = userService.verifyCode(email, code);
//        if (isVerified) {
//            return ResponseEntity.ok(new ApiResponse(true, "이메일 인증이 완료되었습니다."));
//        } else {
//            return ResponseEntity.badRequest().body(new ApiResponse(false, "인증번호가 올바르지 않습니다."));
//        }
//    }
//
//
//}
package com.example.vacation_reservation.controller;
import com.example.vacation_reservation.dto.auth.ForgotPasswordRequest;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.service.email.EmailService;
import com.example.vacation_reservation.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // 비밀번호 찾기 (임시 비밀번호 발송)
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // 이메일로 사용자 찾기
        System.out.println("ForgotPassword 요청 이메일: '" + request.getEmail() + "'");
        User user = userService.findUserByEmail(request.getEmail());

        // 임시 비밀번호 생성
        String temporaryPassword = generateRandomPassword();

        // 이메일로 임시 비밀번호 전송
        emailService.sendTemporaryPassword(user.getEmail(), temporaryPassword);

//        System.out.println("컨트롤러 - email: " + request.getEmail());

        // 유저 비밀번호를 임시 비밀번호로 업데이트
        userService.resetPassword(user.getEmail(), temporaryPassword, temporaryPassword);

        return "임시 비밀번호가 이메일로 발송되었습니다.";
    }

    private String generateRandomPassword() {
        int length = 10;
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * charSet.length());
            password.append(charSet.charAt(randomIndex));
        }

        return password.toString();
    }

}
