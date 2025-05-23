// 공휴일
package com.example.vacation_reservation.dto.holiday;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HolidayDTO {

    private String name;
    private String holidayDate;
}
