/**
 * 공공 API를 통해 특정 연도 또는 월의 공휴일 데이터를 조회하고 데이터베이스에 저장하는 서비스 클래스
 */
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
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 특정 연도와 월의 공휴일 데이터를 공공 API로부터 조회하고 DB에 저장.
     *
     * @param year  조회할 연도 (예: 2025)
     * @param month 조회할 월 (1~12)
     */
    public void fetchHolidays(int year, int month) {
        try {
            String serviceKey = "YumSgOTDcwYLqusBz9TKqdwql4uTmJ/IfGf4GOB8+Bn0wUVY8gbgR0JMgC6wiU2zM7qsomt4hfLHW+t//aFb2w==";
            String encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");

            String url = String.format(
                    "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?" +
                            "serviceKey=%s&solYear=%d&solMonth=%02d",
                    encodedServiceKey, year, month
            );

            // 시크릿코드 왜 안 유효하지 -> url 클래스로 해결
            URI uri = new URI(url);

            // XML 응답 받아오기
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            String responseBody = response.getBody();

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

    /**
     * JSON 응답으로부터 공휴일 데이터를 추출하고, DB에 저장.
     * 이미 존재하는 날짜는 중복 저장하지 않음.
     *
     * @param item JSON에서 개별 공휴일 정보를 담은 노드
     */
    private void saveHolidayFromJson(JsonNode item) {
        String dateStr = item.path("locdate").asText();
        String name = item.path("dateName").asText();
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 중복 체크
        if (holidayRepository.existsByHolidayDate(date)) {
            System.out.println("이미 존재하는 공휴일: " + date + " / " + name);
            return;
        }

        Holiday holiday = new Holiday(null, date, name, true);  // id 가 null

        holidayRepository.save(holiday);
        System.out.println("저장된 공휴일: " + date + " / " + name);
    }

    /**
     * 특정 연도의 1월부터 12월까지 모든 월의 공휴일 데이터를 조회하여 저장.
     *
     * @param year 전체 공휴일을 조회할 연도
     */
    public void fetchAllHolidaysForYear(int year) {
        for (int month = 1; month <= 12; month++) {
            fetchHolidays(year, month);
        }
        System.out.println(year + "년 전체 공휴일 저장 완료");
    }

}