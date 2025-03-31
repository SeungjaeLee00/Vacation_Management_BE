package com.example.vacation_reservation.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final long VALIDITY_IN_MS = 14400 * 1000; // 4시간
    //    private static final String SECRET_KEY = "";

    private static String SECRET_KEY;

    @Value("${jwt.secret}")
    private String secretKeyProperty;

    @PostConstruct
    private void init() {
        SECRET_KEY = secretKeyProperty;
    }

    // JWT 토큰 생성
    public static String generateToken(String employeeId) {
        return Jwts.builder()
                .setSubject(employeeId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY_IN_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // JWT 토큰 검증
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 이미 받은 JWT 토큰에서 사원번호(employeeId)를 추출하는 역할(파싱)
    public static String getEmployeeIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
