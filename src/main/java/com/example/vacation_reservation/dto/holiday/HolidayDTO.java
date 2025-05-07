// 공휴일
package com.example.vacation_reservation.dto.holiday;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class HolidayDTO {

    @NotBlank(message = "공휴일 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "공휴일 날짜는 필수입니다.")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "날짜 형식은 yyyy-MM-dd여야 합니다."
    )
    private String holidayDate;
}
