/**
 * 사용자별 연도 및 휴가 유형에 따른 휴가 잔여 일수를 관리하는 서비스 클래스
 */
package com.example.vacation_reservation.service.vacation;

import com.example.vacation_reservation.dto.vacation.VacationBalanceResponseDto;
import com.example.vacation_reservation.entity.vacation.VacationBalance;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.repository.vacation.VacationBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationBalanceService {

    private final VacationBalanceRepository vacationBalanceRepository;

    /**
     * 사용자별 연도 및 휴가 유형에 따른 잔여 휴가를 조회하고, 요청된 일수만큼 차감하는 메서드
     * @param userId         사용자 ID
     * @param vacationTypeId 휴가 유형 ID
     * @param year           연도
     * @param days           사용하려는 휴가 일수
     * @return 처리 결과 메시지
     */
    @Transactional
    public String checkAndUseVacation(Long userId, Long vacationTypeId, int year, double days) {
        // 잔여 휴가 확인
        VacationBalance balance = getVacationBalance(userId, vacationTypeId, year);
        if (balance == null) {
            throw new CustomException("잔여 휴가 정보가 없습니다.");
        }

        // 충분한 잔여일수 확인
        if (!hasEnoughRemainingDays(balance, days)) {
            throw new CustomException("남은 휴가 일수가 부족합니다.");
        }

        // 휴가 사용 처리
        useVacation(balance, days);

        return "휴가가 정상적으로 처리되었습니다.";
    }

    /**
     * 특정 사용자에 대한 특정 연도의 휴가 잔여 일수를 조회
     * @param userId 사용자 ID
     * @param year   연도
     * @return 휴가 잔여일수 목록 (없을 경우 "잔여 휴가가 없습니다" 메시지를 포함한 리스트 반환)
     */
    @Transactional(readOnly = true)
    public List<VacationBalanceResponseDto> getVacationBalances(Long userId, int year) {
        List<VacationBalance> balances = vacationBalanceRepository.findByUserIdAndYear(userId, year);

        if (balances.isEmpty()) {
            throw new CustomException("잔여 휴가가 없습니다.");
        }

//        if (balances.isEmpty()) {
//            return new ArrayList<VacationBalanceResponseDto>() {{
//                add(new VacationBalanceResponseDto("잔여 휴가가 없습니다", 0));
//            }};
//        }

        return balances.stream()
                .map(balance -> new VacationBalanceResponseDto(
                        balance.getVacationType().getName(),
                        balance.getRemainingDays()))
                .collect(Collectors.toList());
    }

    /**
     * 사용자별 연도 및 휴가 유형에 맞는 휴가 잔여 일수 조회
     * @param userId         사용자 ID
     * @param vacationTypeId 휴가 유형 ID
     * @param year           연도
     * @return VacationBalance 객체
     */
    private VacationBalance getVacationBalance(Long userId, Long vacationTypeId, int year) {
        return vacationBalanceRepository.findByUserIdAndVacationTypeIdAndYear(userId, vacationTypeId, year)
                .orElseThrow(() -> new CustomException("해당 사용자의 잔여 휴가 정보가 없습니다."));
    }

    /**
     * 휴가 잔여일수가 충분한지 확인하는 메서드
     * @param balance 휴가 잔여 일수를 가진 객체
     * @param days    사용하려는 휴가 일수
     * @return 잔여 일수가 충분하면 true, 아니면 false
     */
    private boolean hasEnoughRemainingDays(VacationBalance balance, double days) {
        return balance.getRemainingDays() >= days;
    }

    /**
     * 휴가 차감 처리 메서드
     * @param balance 잔여 휴가 정보
     * @param days    사용하려는 휴가 일수
     */
    private void useVacation(VacationBalance balance, double days) {
        balance.setUsedDays(balance.getUsedDays() + days);
        balance.setRemainingDays(balance.getRemainingDays() - days);
        vacationBalanceRepository.save(balance);
    }
}
