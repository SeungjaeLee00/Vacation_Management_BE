package com.example.vacation_reservation.exception;

import com.example.vacation_reservation.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 예외를 처리하는 글로벌 예외 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     * @param ex CustomException 예외
     * @return API 응답 (에러 메시지)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomException(CustomException ex) {
        // ApiResponse 객체 생성 후 에러 메시지 전달
        ApiResponse apiResponse = new ApiResponse(false, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 그 외의 예외 처리
     * @param ex 발생한 예외
     * @return API 응답 (에러 메시지)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        ApiResponse apiResponse = new ApiResponse(false, "Internal Server Error: " + ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
