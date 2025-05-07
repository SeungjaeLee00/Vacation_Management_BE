// 근데 응답용 DTO도 검증을 하나?...

package com.example.vacation_reservation.dto.vacation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class VacationResponseDto {

    private Long id;

    @NotBlank(message = "신청일자는 비어 있을 수 없습니다.")
    private String requestDate;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;

    @NotBlank(message = "사유는 필수입니다.")
    private String reason;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startAt;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endAt;

    @NotNull(message = "사용 휴가 정보는 필수입니다.")
    @Size(min = 1, message = "최소 하나의 휴가 사용 정보가 필요합니다.")
    private List<VacationUsedDto> usedVacations;

    public VacationResponseDto(Long id, String requestDate, String status, String reason, LocalDate startAt, LocalDate endAt,  List<VacationUsedDto> usedVacations) {
        this.id = id;
        this.requestDate = requestDate;
        this.status = status;
        this.reason = reason;
        this.startAt = startAt;
        this.endAt = endAt;
        this.usedVacations = usedVacations;
    }

//    @Override
//    public String toString() {
//        return "VacationResponseDto{" +
//                "id=" + id +
//                ", requestDate='" + requestDate + '\'' +
//                ", status='" + status + '\'' +
//                ", reason='" + reason + '\'' +
//                ", startAt='" + startAt + '\'' +
//                ", endAt='" + endAt + '\'' +
//                ", usedVacations='" + usedVacations + '\'' +
//                '}';
//    }
}
