package com.example.vacation_reservation.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ChangeNameRequestDto {
    @NotBlank(message = "이름은 필수입니다.")
    private String newName;      // 새로운 이름
}