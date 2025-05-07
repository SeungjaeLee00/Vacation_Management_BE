package com.example.vacation_reservation.validation;

import com.example.vacation_reservation.dto.auth.ChangePasswordRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, ChangePasswordRequestDto> {

    @Override
    public boolean isValid(ChangePasswordRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getNewPassword() == null || dto.getConfirmPassword() == null) {
            return true; // @NotBlank가 따로 처리하므로 여기서는 true 반환
        }

        boolean matched = dto.getNewPassword().equals(dto.getConfirmPassword());

        if (!matched) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("비밀번호가 일치하지 않습니다.")
                    .addPropertyNode("confirmPassword") // 오류 위치 지정
                    .addConstraintViolation();
        }

        return matched;
    }
}
