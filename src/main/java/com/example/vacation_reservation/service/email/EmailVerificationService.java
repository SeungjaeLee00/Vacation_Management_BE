//package com.example.vacation_reservation.service.email;
//
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class EmailVerificationService {
//
//    private Map<String, String> verificationCodes = new HashMap<>();
//
//    // 인증 코드 저장
//    public void saveVerificationCode(String email, String code) {
//        verificationCodes.put(email, code);
//    }
//
//    // 인증 코드 검증
//    public boolean verifyCode(String email, String code) {
//        String storedCode = verificationCodes.get(email);
//        return storedCode != null && storedCode.equals(code);
//    }
//
//    // 인증 코드 삭제 (필요시)
//    public void removeVerificationCode(String email) {
//        verificationCodes.remove(email);
//    }
//}
