package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.UserRequestDto;
import com.example.vacation_reservation.dto.UserResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.repository.UserRepository;
import com.example.vacation_reservation.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    // 생성자 넣기
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String sendVerificationCode(String email) {
        // 이메일 인증 코드 발송
        String code = emailService.sendVerificationCode(email);

        // 인증 코드 저장
        emailVerificationService.saveVerificationCode(email, code);

        return "인증 코드가 발송되었습니다.";
    }

    public boolean verifyCode(String email, String code) {
        // 인증 코드 검증
        return emailVerificationService.verifyCode(email, code);
    }

    // 회원가입 처리
    public String registerUser(UserRequestDto userRequestDto) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            return "이미 존재하는 이메일입니다!";
        }

        // 이메일 인증 확인
        if (!userRequestDto.isEmailVerified()) {
            return "이메일 인증을 완료해주세요.";
        }

        // 비밀번호 확인
        if (!userRequestDto.getPassword().equals(userRequestDto.getConfirmPassword())) {
            return "비밀번호가 일치하지 않습니다.";
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());

        // User 객체 생성 후 저장
        User user = new User(
                userRequestDto.getEmployeeId(),
                userRequestDto.getName(),
                userRequestDto.getEmail(),
                encodedPassword,
                userRequestDto.isEmailVerified()
        );

        userRepository.save(user);

        return "회원가입이 완료되었습니다!";
    }

    // 현재 로그인된 사용자 정보 반환
    public UserResponseDto getCurrentUser(String token) {
        // 토큰에서 employeeId 추출
        String employeeId = JwtTokenProvider.getEmployeeIdFromToken(token);

        // 사용자 정보 가져오기 (employeeId로 사용자 조회)
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 필요한 정보만 담아서 DTO 반환
        return new UserResponseDto(user.getEmployeeId(), user.getEmail(), user.getName());
    }

}
