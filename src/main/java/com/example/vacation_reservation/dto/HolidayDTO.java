package com.example.vacation_reservation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HolidayDTO {
    private String name;
    private String holidayDate;

    public HolidayDTO(String name, String holidayDate) {
        this.name = name;
        this.holidayDate = holidayDate;
    }
}
