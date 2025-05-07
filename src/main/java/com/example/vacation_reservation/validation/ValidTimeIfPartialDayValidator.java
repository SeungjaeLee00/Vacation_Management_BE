package com.example.vacation_reservation.validation;

import com.example.vacation_reservation.dto.vacation.VacationUsedDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class ValidTimeIfPartialDayValidator implements ConstraintValidator<ValidTimeIfPartialDay, VacationUsedDto> {

    @Override
    public boolean isValid(VacationUsedDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        // 1일 미만이면 시간 필수
        if (dto.getUsedDays() < 1) {
            boolean hasStart = dto.getStartTime() != null;
            boolean hasEnd = dto.getEndTime() != null;

            if (!hasStart || !hasEnd) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("휴가 일수가 1 미만이면 시작 시간과 종료 시간은 필수입니다.")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
