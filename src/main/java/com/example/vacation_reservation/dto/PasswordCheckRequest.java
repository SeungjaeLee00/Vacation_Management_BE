package com.example.vacation_reservation.dto;

public class PasswordCheckRequest {

    private String password; // 사용자가 입력한 비밀번호

    // 기본 생성자
    public PasswordCheckRequest() {
    }

    // 생성자 (필드 초기화용)
    public PasswordCheckRequest(String password) {
        this.password = password;
    }

    // Getter (비밀번호 반환)
    public String getPassword() {
        return password;
    }

    // Setter (비밀번호 설정)
    public void setPassword(String password) {
        this.password = password;
    }

    // toString() (디버깅 용으로 객체 정보 출력)
    @Override
    public String toString() {
        return "PasswordCheckRequest{" +
                "password='" + password + '\'' +
                '}';
    }
}
