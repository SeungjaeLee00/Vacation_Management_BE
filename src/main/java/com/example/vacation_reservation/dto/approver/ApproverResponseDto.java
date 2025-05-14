package com.example.vacation_reservation.dto.approver;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApproverResponseDto {
    private String name;
    private String email;
    private String positionName;
    private int positionLevel;
    private String departmentName;
}

