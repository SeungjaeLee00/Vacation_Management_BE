/**
 * 공휴일 관련 API를 제공하는 컨트롤러 클래스
 * 공휴일 정보를 조회하고, 특정 연도의 공휴일 데이터를 저장하는 기능을 제공
 */

package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.holiday.HolidayDTO;
import com.example.vacation_reservation.repository.HolidayRepository;
import com.example.vacation_reservation.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;  // 공휴일 디비에 저장
    private final HolidayRepository holidayRepository;  // 데이터베이스 접근을 위한 레포

    /**
     * 특정 연도의 모든 공휴일 데이터를 저장
     *
     * @param year 저장할 공휴일의 연도
     * @return 공휴일 저장 성공 메시지
     */
    @PostMapping("/fetch-all")
    public ResponseEntity<String> fetchAllHolidays(@RequestParam int year) {
        try {
            holidayService.fetchAllHolidaysForYear(year);
            return ResponseEntity.ok(year + "년 공휴일 저장 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("공휴일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 주어진 기간(시작 연도 및 월, 종료 연도 및 월)에 해당하는 공휴일 정보를 조회
     *
     * @param startYear 시작 연도
     * @param startMonth 시작 월
     * @param endYear 종료 연도
     * @param endMonth 종료 월
     * @return 조회된 공휴일 목록 (HolidayDTO)
     */
    @GetMapping("/get-holiday")
    public List<HolidayDTO> getHolidays(
            @RequestParam int startYear,
            @RequestParam int startMonth,
            @RequestParam int endYear,
            @RequestParam int endMonth
    ) {
        // 시작 날짜와 끝 날짜를 계산
        LocalDate start = LocalDate.of(startYear, startMonth, 1);
        LocalDate end = LocalDate.of(endYear, endMonth, 1).withDayOfMonth(LocalDate.of(endYear, endMonth, 1).lengthOfMonth());

        // 해당 날짜 범위에 있는 공휴일을 DB에서 찾기
        return holidayRepository.findByHolidayDateBetween(start, end)
                .stream()
                .map(h -> new HolidayDTO(
                        h.getName(),
                        h.getHolidayDate().toString()  // 날짜 형식 변환
                ))
                .collect(Collectors.toList());
    }

}


