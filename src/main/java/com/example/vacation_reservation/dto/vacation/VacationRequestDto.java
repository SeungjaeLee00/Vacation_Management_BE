package com.example.vacation_reservation.dto.vacation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class VacationRequestDto {
    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startAt;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endAt;

    @NotBlank(message = "신청 사유는 반드시 입력해야 합니다.")
    @Size(min = 2, max = 100, message = "신청 사유는 2자 이상 100자 이하로 입력하세요.")
    private String reason;

    @NotEmpty(message = "사용할 휴가 유형은 1개 이상 선택해야 합니다.")
    @Valid  // 내부 객체에 대한 validation 적용
    private List<VacationUsedDto> usedVacations;

    @NotNull(message = "결재자는 반드시 지정해야 합니다.")
    private String approverEmployeeId;
}

