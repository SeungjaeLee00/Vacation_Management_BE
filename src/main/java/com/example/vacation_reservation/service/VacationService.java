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
    public Page<VacationResponseDto> getMyVacations(int page, int size ) {
        Pageable pageable = PageRequest.of(page - 1, size);  // 페이지 번호는 0부터 시작하므로 -1 처리

        // 페이지네이션만 적용
        Page<Vacation> vacationPage = vacationRepository.findAll(pageable);

//        if (searchText != null && status != null) {
//            vacationPage = vacationRepository.findByStatusAndVacationTypeOrStatusAndReasonContaining(
//                    status, searchText, status, searchText, pageable
//            );
//        } else if (status != null) {
//            vacationPage = vacationRepository.findByStatus(status, pageable);
//        } else {
//            vacationPage = vacationRepository.findAll(pageable);
//        }

        // Vacation을 VacationResponseDto로 변환하고 반환
        return vacationPage.map(vacation -> new VacationResponseDto(
                vacation.getId(),
                vacation.getRequestDate(),  // LocalDate는 String으로 변환해서 넘김
                vacation.getVacationType(),
                String.join(", ", vacation.getVacationDates()),
                vacation.getStatus(),
                vacation.getReason()
        ));
    }
}
