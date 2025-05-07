package com.example.vacation_reservation.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ ElementType.TYPE }) // 클래스에 적용
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {
    String message() default "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
