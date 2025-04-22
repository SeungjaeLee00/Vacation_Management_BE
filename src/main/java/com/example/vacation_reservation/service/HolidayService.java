package com.example.vacation_reservation.service;

import com.example.vacation_reservation.entity.Holiday;
import com.example.vacation_reservation.repository.HolidayRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public void fetchHolidays(int year, int month) {
        try {
            String serviceKey = "YumSgOTDcwYLqusBz9TKqdwql4uTmJ/IfGf4GOB8+Bn0wUVY8gbgR0JMgC6wiU2zM7qsomt4hfLHW+t//aFb2w==";
            String encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");

            String url = String.format(
                    "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?" +
                            "serviceKey=%s&solYear=%d&solMonth=%02d",
                    encodedServiceKey, year, month
            );

            URI uri = new URI(url);  // url 클래스로 해결

//            System.out.println("요청 URL: " + uri.toString());

            // XML 응답 받아오기
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

//            System.out.println("response" + response);
            String responseBody = response.getBody();
            System.out.println("Raw response: " + responseBody);

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);

            // JSON 응답 처리
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    saveHolidayFromJson(item);
                }
            } else if (!items.isMissingNode()) {
                // 단일 객체인 경우에도 저장
                saveHolidayFromJson(items);
            } else {
                System.out.println("공휴일 데이터가 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 공휴일 저장 로직
    private void saveHolidayFromJson(JsonNode item) {
        String dateStr = item.path("locdate").asText();
        String name = item.path("dateName").asText();
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 중복 체크
        if (holidayRepository.existsByHolidayDate(date)) {
            System.out.println("이미 존재하는 공휴일: " + date + " / " + name);
            return;
        }

        Holiday holiday = new Holiday(null, date, name);
        holidayRepository.save(holiday);
        System.out.println("저장된 공휴일: " + date + " / " + name);
    }

    // 연 전체 공휴일 저장
    public void fetchAllHolidaysForYear(int year) {
        for (int month = 1; month <= 12; month++) {
            fetchHolidays(year, month);
        }
        System.out.println(year + "년 전체 공휴일 저장 완료!");
    }

}