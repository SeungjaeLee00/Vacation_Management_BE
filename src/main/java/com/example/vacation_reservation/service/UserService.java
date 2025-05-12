/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.auth.ChangePasswordRequestDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.vacation_reservation.dto.user.UserResponseDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    /**
     * 사용자 정보를 조회하는 메서드.
     *
     * 사용자의 ID를 기반으로 User 객체를 조회하고, 해당 사용자 정보를 UserResponseDto로 반환
     * User 객체의 position 필드는 fetch join을 통해 함께 로딩
     *
     * @param user 사용자 객체, 현재 로그인된 사용자 정보
     * @return 조회된 사용자 정보를 담고 있는 UserResponseDto
     * @throws CustomException 사용자가 존재하지 않는 경우 발생
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserInfo(User user) {
        User fetchedUser = userRepository.findByIdWithPosition(user.getId())
                .orElseThrow(() -> new CustomException("사용자 정보를 찾을 수 없습니다."));

        return new UserResponseDto(
                fetchedUser.getEmployeeId(),
                fetchedUser.getName(),
                fetchedUser.getEmail(),
                fetchedUser.getPosition().getName(),
                fetchedUser.getProfileImageUrl()
        );
    }


    /**
     * 비밀번호가 일치하는지 확인.
     *
     * @param user        사용자 객체
     * @param rawPassword 입력한 원본 비밀번호
     * @return 비밀번호 일치 여부
     * @throws CustomException 비밀번호 검증 중 발생할 수 있는 예외
     */
    public boolean verifyPassword(User user, String rawPassword) throws CustomException {
        try {
            if (user == null) {
                throw new CustomException("사용자가 존재하지 않습니다.");
            }
            return passwordEncoder.matches(rawPassword, user.getPassword());
        } catch (Exception e) {
            throw new CustomException("비밀번호 검증 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    /**
     * 사용자의 이름을 변경.
     *
     * @param user    사용자 객체
     * @param newName 변경할 이름
     * @return 성공 여부
     * @throws CustomException 사용자 객체가 null이거나 이름 변경 과정에서 오류 발생 시 예외
     */
    public boolean updateUserName(User user, String newName) throws CustomException {
        try {
            if (user == null) {
                throw new CustomException("사용자가 존재하지 않습니다.");
            }

            if (newName == null || newName.trim().isEmpty()) {
                throw new CustomException("새 이름이 유효하지 않습니다.");
            }

            user.setName(newName);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new CustomException("이름 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자의 비밀번호를 변경.
     *
     * @param user                    사용자 객체
     * @param changePasswordRequestDto 비밀번호 변경 요청 DTO (현재/새/확인 비밀번호 포함)
     * @return 성공 여부
     * @throws CustomException 비밀번호 불일치 또는 확인 실패 시 예외 발생
     */
    public boolean changePassword(User user, ChangePasswordRequestDto changePasswordRequestDto) throws CustomException {
        try {
            // 현재 비밀번호 검증
            if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), user.getPassword())) {
                throw new CustomException("현재 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
            if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword())) {
                throw new CustomException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호 규칙 확인
            if (!isValidPassword(changePasswordRequestDto.getNewPassword())) {
                throw new CustomException("새 비밀번호는 최소 8자 이상이며, 알파벳 소문자, 숫자, 특수문자를 포함해야 합니다.");
            }

            // 새 비밀번호를 암호화하여 저장
            user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new CustomException("비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    /**
     * 비밀번호를 재설정. (비밀번호 찾기나 초기화 시 사용)
     *
     * @param email          사용자 이메일
     * @param newPassword    새 비밀번호
     * @param confirmPassword 확인용 비밀번호
     * @return 성공 여부
     * @throws CustomException 사용자가 존재하지 않거나 비밀번호 불일치 시
     */
    public boolean resetPassword(String email, String newPassword, String confirmPassword) throws CustomException {
        try {
            // 이메일로 사용자 찾기
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

            // 새 비밀번호와 확인 비밀번호가 일치하는지 확인
            if (!newPassword.equals(confirmPassword)) {
                throw new CustomException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호 규칙 확인
            if (!isValidPassword(newPassword)) {
                throw new CustomException("새 비밀번호는 최소 8자 이상이며, 알파벳 소문자, 숫자, 특수문자를 포함해야 합니다.");
            }

            // 새 비밀번호 암호화 후 저장
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return true;
        } catch (Exception e) {
            throw new CustomException("비밀번호 재설정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    /**
     * 비밀번호 규칙 검사
     * - 알파벳 소문자, 숫자, 특수문자가 포함되어야 하고, 최소 8자 이상이어야 함
     *
     * @param password 비밀번호
     * @return 규칙에 맞는지 여부
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false; // 비밀번호 길이가 8자 미만일 경우
        }

        // 알파벳 소문자, 숫자, 특수문자가 각각 하나 이상 포함되도록 정규식 작성
        String passwordPattern = "(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).+";
        return Pattern.matches(passwordPattern, password);
    }


    /**
     * 이메일로 사용자를 조회.
     *
     * @param email 사용자 이메일
     * @return 사용자 객체
     * @throws CustomException 사용자를 찾을 수 없는 경우
     */
    public User findUserByEmail(String email) throws CustomException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));
    }


    /**
     * 사용자 프로필 이미지 업데이트
     *
     * @param user 사용자 객체. 프로필 이미지를 업데이트할 대상 사용자.
     * @param newImage 사용자가 업로드한 새로운 프로필 이미지 파일.
     * @throws CustomException 이미지 업로드 중 발생한 오류를 처리하는 커스텀 예외.
     */
    @Transactional
    public void updateProfileImage(User user, MultipartFile newImage) {
        try {
            // S3에 파일 업로드
            String httpsUrl = s3Service.uploadFile(newImage);
            String httpUrl = convertToHttpUrl(httpsUrl);

            // DB에 이미지 URL 저장
            user.setProfileImageUrl(httpUrl);
            userRepository.save(user);

        } catch (IOException e) {
            throw new CustomException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    /**
     * S3에서 반환된 HTTPS URL을 HTTP URL로 변환하는 메서드
     *
     * @param httpsUrl S3에서 반환된 HTTPS 형식의 URL.
     * @return HTTP 형식으로 변환된 URL.
     */
    private String convertToHttpUrl(String httpsUrl) {
        return httpsUrl.replace(
                "https://2025ojt.leave-application.s3.ap-northeast-2.amazonaws.com",
                "http://2025ojt.leave-application.s3-website.ap-northeast-2.amazonaws.com"
        );
    }

}
