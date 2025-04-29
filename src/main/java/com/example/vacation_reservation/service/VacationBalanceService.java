package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.vacation.VacationBalanceResponseDto;
import com.example.vacation_reservation.entity.VacationBalance;
import com.example.vacation_reservation.repository.VacationBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VacationBalanceService {

    private final VacationBalanceRepository vacationBalanceRepository;

    public VacationBalanceService(VacationBalanceRepository vacationBalanceRepository) {
        this.vacationBalanceRepository = vacationBalanceRepository;
    }

    // 잔여 휴가 계산, 사용
    @Transactional
    public String checkAndUseVacation(Long userId, Long vacationTypeId, int year, double days) {
        // 유저별, 종류별, 연도별 잔여 휴가 조회
        VacationBalance balance = vacationBalanceRepository.findByUserIdAndVacationTypeIdAndYear(userId, vacationTypeId, year)
                .orElse(null);
        if (balance == null) {
            return "잔여 휴가가 없습니다.";
        }

        // 잔여일수 충분한지 체크
        if (balance.getRemainingDays() < days) {
            return "남은 휴가 일수가 부족합니다.";
        }

        // 사용일수 업데이트
        balance.setUsedDays(balance.getUsedDays() + days);

        // 남은일수 업데이트
        balance.setRemainingDays(balance.getRemainingDays() - days);

        vacationBalanceRepository.save(balance);

        return "휴가가 정상적으로 처리되었습니다.";
    }

    // 잔여 휴가 조회
    @Transactional(readOnly = true)
    public List<VacationBalanceResponseDto> getVacationBalances(Long userId, int year) {
        // 유저별, 연도별로 모든 종류의 잔여 휴가 조회
        List<VacationBalance> balances = vacationBalanceRepository.findByUserIdAndYear(userId, year);

        if (balances.isEmpty()) {
            List<VacationBalanceResponseDto> response = new ArrayList<>();
            response.add(new VacationBalanceResponseDto("잔여 휴가가 없습니다", 0));
            return response;
        }

        return balances.stream()
                .map(balance -> new VacationBalanceResponseDto(
                        balance.getVacationType().getName(),
                        balance.getRemainingDays()))
                .collect(Collectors.toList());
    }
}
