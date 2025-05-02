/**
 * 사용자 정의 예외 클래스
 * 이 클래스는 잘못된 요청에 대해 발생하는 예외를 처리하며,
 * 발생 시 HTTP 400 (BAD_REQUEST) 상태 코드를 반환함.
 * <p>
 * {@link ResponseStatus} 어노테이션을 사용하여 이 예외가 발생하면 클라이언트에게
 * HTTP 400 상태 코드를 반환하도록 지정됨.
 * </p>
 */

package com.example.vacation_reservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
