package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.VacationResponseDto;
import com.example.vacation_reservation.entity.Vacation;
import com.example.vacation_reservation.repository.VacationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class VacationService {

    @Autowired
    private VacationRepository vacationRepository;

    // 휴가 신청 저장
    public void saveVacationRequest(Vacation vacation) {
        vacationRepository.save(vacation);
    }

    // 내가 신청한 휴가 목록 조회
    public Page<VacationResponseDto> getMyVacations(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Vacation> vacationPage = vacationRepository.findAll(pageable);

        return vacationPage.map(vacation -> {
            // VacationUsed 리스트가 null 아니면 처리
            String usedVacationSummary = "";
            if (vacation.getUsedVacations() != null && !vacation.getUsedVacations().isEmpty()) {
                usedVacationSummary = vacation.getUsedVacations().stream()
                        .map(used -> used.getVacationType().getName() + " " + used.getUsedDays() + "일")
                        .collect(Collectors.joining(", "));
            }

            return new VacationResponseDto(
                    vacation.getId(),
                    vacation.getRequestDate().toString(),  // LocalDate -> String 변환
                    usedVacationSummary,                   // 변경된 부분
                    vacation.getStatus(),
                    vacation.getReason()
            );
        });
    }

}
