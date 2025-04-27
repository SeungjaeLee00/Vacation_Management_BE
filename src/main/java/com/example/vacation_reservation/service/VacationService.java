package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.vacation.VacationResponseDto;
import com.example.vacation_reservation.dto.vacation.VacationUsedDto;
import com.example.vacation_reservation.entity.Vacation;
import com.example.vacation_reservation.repository.VacationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacationService {

    @Autowired
    private VacationRepository vacationRepository;

    public VacationService(VacationRepository vacationRepository) {
        this.vacationRepository = vacationRepository;
    }

    // 휴가 신청 저장
    @Transactional
    public Vacation saveVacationRequest(Vacation vacation) {
        return vacationRepository.save(vacation);
    }

    // 내가 신청한 휴가 목록 조회
    public Page<VacationResponseDto> getMyVacations(String employeeId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Vacation> vacationPage = vacationRepository.findByUser_EmployeeId(employeeId, pageable);

        return vacationPage.map(vacation -> {
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
        });
    }

}

