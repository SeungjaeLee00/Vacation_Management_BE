/**
 * 인증 서비스 클래스
 * 사용자의 로그인 기능을 담당하며, 사용자 인증 후 JWT 토큰을 생성함.
 */
package com.example.vacation_reservation.service;

import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.repository.UserRepository;
import com.example.vacation_reservation.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 주어진 사번과 비밀번호를 기반으로 로그인 처리.
     * <ul>
     *     <li>사번에 해당하는 사용자를 조회.</li>
     *     <li>비밀번호가 일치하는지 확인.</li>
     *     <li>일치하면 JWT 토큰을 생성하여 반환합.</li>
     * </ul>
     *
     * @param employeeId 로그인하려는 사용자의 사번
     * @param password   사용자가 입력한 비밀번호
     * @return JWT 토큰 문자열
     * @throws RuntimeException 사번에 해당하는 사용자가 없거나 비밀번호가 틀린 경우 예외 발생
     */
    public String login(String employeeId, String password) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("해당 사번을 가진 사용자가 존재하지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        // 로그인 성공 시 JWT 토큰 생성
        return JwtTokenProvider.generateToken(user.getEmployeeId());
    }
}

// 세션을 주로 쓰고, 세션+토큰 이렇게도 씀
// refresh token 을 4시간으로 설정