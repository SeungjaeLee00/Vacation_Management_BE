package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.vacation.VacationRequestDto;
import com.example.vacation_reservation.dto.vacation.VacationResponseDto;
import com.example.vacation_reservation.dto.vacation.VacationUsedDto;
import com.example.vacation_reservation.entity.*;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.repository.VacationRepository;
import com.example.vacation_reservation.repository.VacationTypeRepository;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacationService {

    private VacationRepository vacationRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final VacationBalanceService vacationBalanceService;

    public VacationService(VacationRepository vacationRepository, VacationTypeRepository vacationTypeRepository, VacationBalanceService vacationBalanceService) {
        this.vacationRepository = vacationRepository;
        this.vacationTypeRepository = vacationTypeRepository;
        this.vacationBalanceService = vacationBalanceService;
    }

    // 휴가 신청 저장
    @Transactional
    public void requestVacation(User user, VacationRequestDto dto) {
        // 총 사용일 계산
        double totalUsedDays = 0;
        for (VacationUsedDto usedDto : dto.getUsedVacations()) {
            totalUsedDays += usedDto.getUsedDays();
        }

        // 1일 미만 휴가 예외처리
        if (totalUsedDays < 1.0) {
            for (VacationUsedDto usedDto : dto.getUsedVacations()) {
                if (usedDto.getStartTime() == null || usedDto.getEndTime() == null) {
                    throw new CustomException("1일 미만 휴가 사용 시 시작 시간과 종료 시간을 반드시 입력해야 합니다.");
                }
            }
        }

        // 잔여 휴가 확인
        for (VacationUsedDto usedDto : dto.getUsedVacations()) {
            VacationType vacationType = vacationTypeRepository.findByName(usedDto.getVacationTypeName())
                    .orElseThrow(() -> new RuntimeException("휴가 종류를 찾을 수 없습니다: " + usedDto.getVacationTypeName()));

//            if (usedDto.getUsedDays() < 1.0) {
//                if (usedDto.getStartTime() == null || usedDto.getEndTime() == null) {
//                    throw new CustomException("1일 미만 휴가 사용 시 시작 시간과 종료 시간을 반드시 입력해야 합니다.");
//                }
//            }

            String vacationBalanceMessage = vacationBalanceService.checkAndUseVacation(
                    user.getId(),
                    vacationType.getId(),
                    LocalDate.now().getYear(),
                    usedDto.getUsedDays()
            );

            if (!vacationBalanceMessage.equals("휴가가 정상적으로 처리되었습니다.")) {
                throw new CustomException(vacationBalanceMessage);
            }
        }

        // 휴가 신청 객체 생성
        Vacation vacation = new Vacation();
        vacation.setUser(user);
        vacation.setStartAt(dto.getStartAt());
        vacation.setEndAt(dto.getEndAt());
        vacation.setReason(dto.getReason());
        vacation.setRequestDate(LocalDate.now());
        vacation.setStatus("Pending");

        // VacationUsed 리스트 생성
        List<VacationUsed> usedVacations = dto.getUsedVacations().stream().map(usedDto -> {
            VacationUsed vu = new VacationUsed();
            vu.setVacation(vacation);

            VacationType vt = vacationTypeRepository.findByName(usedDto.getVacationTypeName())
                    .orElseThrow(() -> new RuntimeException("휴가 종류를 찾을 수 없습니다: " + usedDto.getVacationTypeName()));
            vu.setVacationType(vt);

            vu.setUsedDays(usedDto.getUsedDays());
            vu.setStartTime(usedDto.getStartTime());
            vu.setEndTime(usedDto.getEndTime());

            return vu;
        }).collect(Collectors.toList());

        vacation.setUsedVacations(usedVacations);

        // 저장
        vacationRepository.save(vacation);
    }

    // 내가 신청한 휴가 목록 조회
    public List<VacationResponseDto> getAllMyVacations(String employeeId) {
        List<Vacation> vacations = vacationRepository.findByUser_EmployeeId(employeeId);

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
}
