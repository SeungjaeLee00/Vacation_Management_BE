/**
 * 사용자 관련 API를 제공하는 컨트롤러 클래스
 * 비밀번호 찾기 기능을 포함
 */
package com.example.vacation_reservation.controller;
import com.example.vacation_reservation.dto.ApiResponse;
import com.example.vacation_reservation.dto.auth.ChangeNameRequestDto;
import com.example.vacation_reservation.dto.auth.ChangePasswordRequestDto;
import com.example.vacation_reservation.dto.auth.ForgotPasswordRequest;
import com.example.vacation_reservation.dto.user.UserResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.security.CustomUserDetails;
import com.example.vacation_reservation.service.email.EmailService;
import com.example.vacation_reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인된 사용자의 정보 조회
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 정보 DTO (사원번호, 이름, 이메일, 직급)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                throw new CustomException("사용자 정보가 없습니다. 로그인 상태를 확인해주세요.");
            }

            UserResponseDto dto = userService.getUserInfo(userDetails.getUser());
            return ResponseEntity.ok(dto);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "사용자 정보 조회 실패: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> changeName(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ChangeNameRequestDto dto) {
        try {
            if (userDetails == null) {
                throw new CustomException("인증된 사용자가 아닙니다.");
            }
            userService.updateUserName(userDetails.getUser(), dto.getNewName());

            return ResponseEntity.ok("이름이 변경되었습니다.");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "이름 변경 실패: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 현재 로그인한 사용자의 비밀번호를 변경
     *
     * @param userDetails 인증된 사용자 정보
     * @param dto         비밀번호 변경 요청 DTO (현재 비밀번호, 새 비밀번호 포함)
     * @return 비밀번호 변경 성공 또는 실패 메시지
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ChangePasswordRequestDto dto) {
        try {
            if (userDetails == null) {
                throw new CustomException("인증된 사용자가 아닙니다.");
            }
            userService.changePassword(userDetails.getUser(), dto);

            return ResponseEntity.ok(new ApiResponse(true, "비밀번호가 변경되었습니다."));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "비밀번호 변경 실패: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 프로필 이미지를 변경하는 메서드
     *
     * @param userDetails 현재 인증된 사용자의 세부 정보를 담고 있는 객체.
     * @param image 업로드할 프로필 이미지 파일.
     * @return 변경 완료 메시지를 담은 ResponseEntity.
     */
    @PostMapping("/change-profile-image")
    public ResponseEntity<?> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("image") MultipartFile image) {

        userService.updateProfileImage(userDetails.getUser(), image);
        return ResponseEntity.ok("프로필 이미지가 변경되었습니다.");
    }
}
