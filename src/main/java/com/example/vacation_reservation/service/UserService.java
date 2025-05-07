/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.auth.ChangePasswordRequestDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호가 일치하는지 확인.
     *
     * @param user        사용자 객체
     * @param rawPassword 입력한 원본 비밀번호
     * @return 비밀번호 일치 여부
     */
    // public -> private => 안됨.. 컨트롤러에서 사용 중임
    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    /**
     * 사용자의 이름을 변경.
     *
     * @param user    사용자 객체
     * @param newName 변경할 이름
     * @return 성공 여부
     */
    public boolean updateUserName(User user, String newName) {
        user.setName(newName);
        userRepository.save(user);
        return true;
    }

    /**
     * 사용자의 비밀번호를 변경.
     *
     * @param user                    사용자 객체
     * @param changePasswordRequestDto 비밀번호 변경 요청 DTO (현재/새/확인 비밀번호 포함)
     * @return 성공 여부
     * @throws RuntimeException 비밀번호 불일치 또는 확인 실패 시 예외 발생
     */
    // public -> private => 안됨.. 컨트롤러에서 사용 중임
    public boolean changePassword(User user, ChangePasswordRequestDto changePasswordRequestDto) {
        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
        if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword())) {
            throw new RuntimeException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호를 암호화하여 저장
        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    /**
     * 비밀번호를 재설정. (비밀번호 찾기나 초기화 시 사용)
     *
     * @param email          사용자 이메일
     * @param newPassword    새 비밀번호
     * @param confirmPassword 확인용 비밀번호
     * @return 성공 여부
     * @throws RuntimeException 사용자가 존재하지 않거나 비밀번호 불일치 시
     */
    // public -> private => 안됨.. 컨트롤러에서 사용 중임
    public boolean resetPassword(String email, String newPassword, String confirmPassword) {
        // 이메일로 사용자 찾기
        System.out.println("찾으려는 이메일: '" + email + "'");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }

    /**
     * 이메일로 사용자를 조회.
     *
     * @param email 사용자 이메일
     * @return 사용자 객체
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}
