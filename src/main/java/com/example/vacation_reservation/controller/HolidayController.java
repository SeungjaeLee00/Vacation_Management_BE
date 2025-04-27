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
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;  // 공휴일 디비에 저장
    private final HolidayRepository holidayRepository;  // 데이터베이스 접근을 위한 레포

//    // 특정 월 공휴일 저장
//    @GetMapping("/fetch")
//    public String fetchHolidays(
//            @RequestParam int year,
//            @RequestParam int month
//    ) {
//        holidayService.fetchHolidays(year, month);
//        return String.format("%d년 %d월 공휴일 데이터 저장", year, month);
//    }

    // 전체 공휴일 저장
    @PostMapping("/fetch-all")
    public ResponseEntity<String> fetchAllHolidays(@RequestParam int year) {
        try {
            holidayService.fetchAllHolidaysForYear(year);
            return ResponseEntity.ok(year + "년 공휴일 저장 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("공휴일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 공휴일 조회
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


