/**
 * 공공 API를 통해 특정 연도 또는 월의 공휴일 데이터를 조회하고 데이터베이스에 저장하는 서비스 클래스
 * fetchHolidays 메소드에서만 throws CustomException을 사용한 이유는
 * 이 메소드는 외부 API와 상호작용하기 때문에 여러 가지 예외가 발생할 수 있고,
 * 이를 호출한 곳에서 일관되게 처리할 수 있도록 하기 위해서임
 */
package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.holiday.HolidayDTO;
import com.example.vacation_reservation.entity.Holiday;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.repository.HolidayRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

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
     * @throws CustomException 공휴일 조회 및 저장 과정에서 발생한 예외
     */
    public void fetchHolidays(int year, int month) throws CustomException {
        try {
            String serviceKey = "YumSgOTDcwYLqusBz9TKqdwql4uTmJ/IfGf4GOB8+Bn0wUVY8gbgR0JMgC6wiU2zM7qsomt4hfLHW+t//aFb2w==";
            String encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");

            String url = String.format(
                    "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?" +
                            "serviceKey=%s&solYear=%d&solMonth=%02d",
                    encodedServiceKey, year, month
            );

            // URL 및 URI 처리
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
                throw new CustomException("공휴일 데이터가 없습니다.");
            }

        } catch (URISyntaxException e) {
            throw new CustomException("URL 구성이 잘못되었습니다: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new CustomException("서비스 키 인코딩 실패: " + e.getMessage());
        } catch (RestClientException e) {
            throw new CustomException("API 호출 실패: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new CustomException("응답 JSON 파싱 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new CustomException("알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }


    /**
     * JSON 응답으로부터 공휴일 데이터를 추출하고, DB에 저장.
     * 이미 존재하는 날짜는 중복 저장하지 않음.
     *
     * @param item JSON에서 개별 공휴일 정보를 담은 노드
     * @throws CustomException 공휴일 저장 과정에서 발생할 수 있는 예외 처리
     */
    private void saveHolidayFromJson(JsonNode item) throws CustomException {
        try {
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
        } catch (DateTimeParseException e) {
            throw new CustomException("공휴일 날짜 파싱 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new CustomException("공휴일 저장 중 오류 발생: " + e.getMessage());
        }
    }


    /**
     * 특정 연도의 1월부터 12월까지 모든 월의 공휴일 데이터를 조회하여 저장.
     *
     * @param year 전체 공휴일을 조회할 연도
     * @throws CustomException 공휴일 조회 및 저장 과정에서 발생할 수 있는 예외 처리
     */
    public void fetchAllHolidaysForYear(int year) throws CustomException {
        try {
            for (int month = 1; month <= 12; month++) {
                fetchHolidays(year, month);
            }
            System.out.println(year + "년 전체 공휴일 저장 완료");
        } catch (CustomException e) {
            throw new CustomException("공휴일 저장 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            throw new CustomException("전체 공휴일을 저장하는 중 알 수 없는 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 지정된 기간(start부터 end까지)에 해당하는 공휴일 목록을 조회하여 HolidayDTO 리스트로 반환.
     *
     * @param start 조회 시작 날짜 (포함)
     * @param end   조회 종료 날짜 (포함)
     * @return 조회된 공휴일 정보를 담은 HolidayDTO 리스트
     */
    public List<HolidayDTO> getHolidaysBetween(LocalDate start, LocalDate end) {
        return holidayRepository.findByHolidayDateBetween(start, end).stream()
                .map(h -> {
                    HolidayDTO dto = new HolidayDTO();
                    dto.setName(h.getName());
                    dto.setHolidayDate(h.getHolidayDate().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}