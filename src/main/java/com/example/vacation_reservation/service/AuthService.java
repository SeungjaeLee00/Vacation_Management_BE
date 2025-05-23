/**
 * 인증 서비스 클래스
 * 사용자의 로그인 기능을 담당하며, 사용자 인증 후 JWT 토큰을 생성함.
 */
package com.example.vacation_reservation.service;

import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.repository.UserRepository;
import com.example.vacation_reservation.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Validated
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


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
     * @throws CustomException 사번에 해당하는 사용자가 없거나 비밀번호가 틀린 경우 예외 발생
     */
    public Map<String, String> login(String employeeId, String password) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("아이디 또는 비밀번호가 일치하지 않습니다."));  // 사실은 사번이긴 함

        // 계정 활성화 여부 체크
        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new CustomException("계정이 비활성화 상태입니다. 관리자에게 문의하세요.");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("비밀번호가 올바르지 않습니다.");
        }

        // 로그인 성공 시 JWT 토큰 생성
        // Access, Refresh Token 생성
        String accessToken = JwtTokenProvider.generateAccessToken(user.getEmployeeId());
        String refreshToken = JwtTokenProvider.generateRefreshToken(user.getEmployeeId());

        // Refresh Token DB에 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 두 토큰 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }


    /**
     * Refresh Token을 이용해 새로운 Access Token 발급
     *
     * @param refreshToken 클라이언트로부터 전달받은 Refresh Token
     * @return 새로운 Access Token
     * @throws CustomException Refresh Token이 유효하지 않거나 만료된 경우
     */
    public String refreshAccessToken(String refreshToken) {
        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException("리프레시 토큰이 유효하지 않거나 만료되었습니다.");
        }

        // 토큰에서 employeeId 추출
        String employeeId = jwtTokenProvider.getEmployeeIdFromToken(refreshToken);

        // 디비에서 사용자 조회
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("해당 사용자가 존재하지 않습니다."));

        // 디비에서 저장되어 있는 토큰이랑 비교
        String savedToken = user.getRefreshToken();
        if (!refreshToken.equals(savedToken)) {
            throw new CustomException("저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        // 새 accessToken 발급
        return jwtTokenProvider.generateAccessToken(employeeId);
    }



    /**
     * Refresh Token 삭제
     *
     * @param employeeId 사원번호
     * @throws CustomException 사용자가 존재하지 않거나 토큰 삭제 실패 시 예외 발생
     */
    public void clearRefreshToken(String employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("해당 사번을 가진 사용자가 존재하지 않습니다."));

        try {
            user.setRefreshToken(null);
            userRepository.save(user);
        } catch (Exception e) {
            throw new CustomException("Refresh Token 삭제 중 오류가 발생했습니다.");
        }
    }
}