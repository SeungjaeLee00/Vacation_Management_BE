package com.example.vacation_reservation.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangeNameRequestDto {
    private String newName;      // 새로운 이름
}