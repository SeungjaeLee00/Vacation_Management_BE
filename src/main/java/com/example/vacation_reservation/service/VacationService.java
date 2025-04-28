package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.vacation.VacationRequestDto;
import com.example.vacation_reservation.dto.vacation.VacationResponseDto;
import com.example.vacation_reservation.dto.vacation.VacationUsedDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.entity.Vacation;
import com.example.vacation_reservation.entity.VacationType;
import com.example.vacation_reservation.entity.VacationUsed;
import com.example.vacation_reservation.repository.VacationRepository;
import com.example.vacation_reservation.repository.VacationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

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

    public VacationService(VacationRepository vacationRepository, VacationTypeRepository vacationTypeRepository) {
        this.vacationRepository = vacationRepository;
        this.vacationTypeRepository = vacationTypeRepository;
    }

    // 휴가 신청 저장
    @Transactional
    public void requestVacation(User user, VacationRequestDto dto) {
        Vacation vacation = new Vacation();
        vacation.setUser(user);
        vacation.setStartAt(dto.getStartAt());
        vacation.setEndAt(dto.getEndAt());
        vacation.setReason(dto.getReason());
        vacation.setRequestDate(LocalDate.now());
        vacation.setStatus("Pending");

        // VacationUsed 리스트 변환
        List<VacationUsed> usedVacations = dto.getUsedVacations().stream().map(usedDto -> {
            VacationUsed vu = new VacationUsed();
            vu.setVacation(vacation);

            VacationType vt = vacationTypeRepository.findByName(usedDto.getVacationTypeName())
                    .orElseThrow(() -> new RuntimeException("휴가 종류를 찾을 수 없습니다: " + usedDto.getVacationTypeName()));
            vu.setVacationType(vt);

            vu.setUsedDays(usedDto.getUsedDays());
            return vu;
        }).collect(Collectors.toList());

        vacation.setUsedVacations(usedVacations);

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
                                used.getUsedDays()
                        ))
                        .collect(Collectors.toList());
            }

            return new VacationResponseDto(
                    vacation.getId(),
                    vacation.getRequestDate().toString(),
                    vacation.getStatus(),
                    vacation.getReason(),
                    vacation.getStartAt().toString(),
                    vacation.getEndAt().toString(),
                    usedVacations
            );
        }).collect(Collectors.toList());
    }
}



