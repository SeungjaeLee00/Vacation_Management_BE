/**
 * 휴가 신청 및 조회 기능을 처리
 */

package com.example.vacation_reservation.service.vacation;

import com.example.vacation_reservation.dto.vacation.VacationInfoDto;
import com.example.vacation_reservation.dto.vacation.VacationRequestDto;
import com.example.vacation_reservation.dto.vacation.VacationResponseDto;
import com.example.vacation_reservation.dto.vacation.VacationUsedDto;
import com.example.vacation_reservation.entity.*;
import com.example.vacation_reservation.entity.vacation.*;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.mapper.VacationMapper;
import com.example.vacation_reservation.repository.UserRepository;
import com.example.vacation_reservation.repository.vacation.VacationRepository;
import com.example.vacation_reservation.repository.vacation.VacationTypeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final UserRepository userRepository;
    private final VacationRepository vacationRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final VacationBalanceService vacationBalanceService;

    private final VacationMapper vacationMapper;

    /**
     * 사용자의 휴가 신청을 처리
     *
     * <p>1일 미만 휴가인 경우 시작시간과 종료시간을 반드시 입력해야 하며, 잔여 휴가가 부족한 경우 예외를 발생시킴.</p>
     *
     * @param user 사용자 정보
     * @param dto  휴가 신청 DTO
     * @throws CustomException 잔여 휴가 부족, 1일 미만 휴가의 필수 정보 누락 시 예외 발생
     */
    @Transactional
    public void requestVacation(User user, VacationRequestDto dto) {
        double totalUsedDays = 0;
        for (VacationUsedDto usedDto : dto.getUsedVacations()) {
            totalUsedDays += usedDto.getUsedDays();
        }

        // 1일 미만 휴가인 경우 시작/종료 시간 필수
        if (totalUsedDays < 1.0) {
            for (VacationUsedDto usedDto : dto.getUsedVacations()) {
                if (usedDto.getStartTime() == null || usedDto.getEndTime() == null) {
                    throw new CustomException("1일 미만 휴가 사용 시 시작 시간과 종료 시간을 반드시 입력해야 합니다.");
                }
            }
        }

        // 각 휴가 종류별 잔여 일수 확인 및 차감
        for (VacationUsedDto usedDto : dto.getUsedVacations()) {
            VacationType vacationType = vacationTypeRepository.findByName(usedDto.getVacationTypeName())
                    .orElseThrow(() -> new CustomException("휴가 종류를 찾을 수 없습니다: " + usedDto.getVacationTypeName()));

            String vacationBalanceMessage = vacationBalanceService.checkAndUseVacation(
                    user.getId(),
                    vacationType.getId(),
                    LocalDate.now().getYear(),
                    usedDto.getUsedDays()  // 디비에서 가져올 수 있게~
            );

            if (!vacationBalanceMessage.equals("휴가가 정상적으로 처리되었습니다.")) {
                throw new CustomException(vacationBalanceMessage);
            }
        }

        // 휴가 신청 정보 저장
        Vacation vacation = new Vacation(
                user,
                dto.getStartAt(),
                dto.getEndAt(),
                dto.getReason(),
                LocalDate.now(),
                VacationStatus.PENDING
        );


        // 사용 휴가 목록 구성
        List<VacationUsed> usedVacations = dto.getUsedVacations().stream().map(usedDto -> {
            VacationUsed vu = new VacationUsed();
            vu.setVacation(vacation);

            VacationType vt = vacationTypeRepository.findByName(usedDto.getVacationTypeName())
                    .orElseThrow(() -> new CustomException("휴가 종류를 찾을 수 없습니다: " + usedDto.getVacationTypeName()));
            vu.setVacationType(vt);

            vu.setUsedDays(usedDto.getUsedDays());
            vu.setStartTime(usedDto.getStartTime());
            vu.setEndTime(usedDto.getEndTime());

            return vu;
        }).collect(Collectors.toList());

        vacation.setUsedVacations(usedVacations);

        vacationRepository.save(vacation);
    }

    /**
     * 해당 사용자가 신청한 모든 휴가 목록을 조회.
     *
     * @param employeeId 사원번호
     * @return 휴가 응답 DTO 리스트
     * @throws CustomException 사용자가 존재하지 않거나, 휴가 내역이 없을 경우
     */
    public List<VacationResponseDto> getAllMyVacations(String employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("해당 사원번호에 해당하는 사용자가 존재하지 않습니다."));

        List<Vacation> vacations = vacationRepository.findByUser_EmployeeId(employeeId);

        if (vacations.isEmpty()) {
            return new ArrayList<>();
        }

        return vacations.stream().map(vacation -> {
            List<VacationUsedDto> usedVacations = new ArrayList<>();
            if (vacation.getUsedVacations() != null && !vacation.getUsedVacations().isEmpty()) {
                usedVacations = vacation.getUsedVacations().stream()
                        .map(used -> new VacationUsedDto(
                                used.getVacationType().getName(),
                                used.getUsedDays(),
                                used.getStartTime(),
                                used.getEndTime()
                        ))
                        .collect(Collectors.toList());
            }

            return new VacationResponseDto(
                    vacation.getId(),
                    vacation.getRequestDate().toString(),
                    vacation.getStatus(),
                    vacation.getReason(),
                    vacation.getStartAt(),
                    vacation.getEndAt(),
                    usedVacations
            );
        }).collect(Collectors.toList());
    }

    /**
     * 사용자의 부서에 속한 모든 구성원의 휴가 목록을 조회
     *
     * 먼저, 전달된 사용자 객체에서 부서 ID를 조회하고
     * 해당 부서에 속한 사람들의 휴가 목록을 MyBatis를 사용하여 가져옴
     *
     * @param user 부서 휴가 목록을 조회할 사용자의 정보
     * @return 해당 부서에 속한 사용자들의 휴가 목록 (List<VacationInfoDto>)
     * @throws CustomException  사용자를 찾을 수 없을 때 발생
     */
    public List<VacationInfoDto> getVacationsInMyDepartment(User user) {
        // 사용자의 부서 ID 가져오기 (JPA로)
        Long departmentId = userRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException ("사용자를 찾을 수 없습니다."))
                .getDepartment()
                .getId();

        // MyBatis로 해당 부서 사람들의 휴가 목록 조회
        List<VacationInfoDto> vacations = vacationMapper.findVacationsByDepartmentId(departmentId);

        // 부서 내에 휴가자가 없으면 예외 처리
        if (vacations.isEmpty()) {
            throw new CustomException("부서 내에 휴가자가 없습니다.");
        }

        return vacations;
    }
}
