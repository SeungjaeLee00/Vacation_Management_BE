package com.example.vacation_reservation.config;

import com.example.vacation_reservation.security.JwtAuthenticationFilter;
import com.example.vacation_reservation.security.JwtTokenProvider;
import com.example.vacation_reservation.service.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 설정 클래스.
 * JWT 기반의 인증 및 인가를 적용하며, 세션은 사용하지 않음.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Spring Security 필터 체인 설정
     *
     * - CSRF 비활성화
     * - 세션 상태 없음으로 설정 (JWT 사용)
     * - 인증이 필요 없는 URL 설정
     * - 나머지는 인증 필요
     * - JWT 필터 등록
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 설정 중 오류 발생 시
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안함 (JWT 방식)
                .and()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/api/auth/login",
                        "/api/auth/forgot-password",
                        "/api/auth/refresh-token",
                        "/api/holidays/**"
                ).permitAll()  // 로그인 관련 API, 공공 API는 인증 없이 접근 가능
                .anyRequest().authenticated()  // 나머지 요청은 인증 필요
                .and()
//                .userDetailsService(customUserDetailsService)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationManager 빈 등록
     *
     * @param authenticationConfiguration 인증 구성
     * @return AuthenticationManager 객체
     * @throws Exception 설정 중 오류 발생 시
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * CORS 설정
     *
     * @return CorsConfigurationSource 객체
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:8081"); // 프론트엔드 주소

        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        corsConfiguration.addAllowedHeader("*"); // 모든 헤더 허용
        corsConfiguration.setAllowCredentials(true); // 쿠키 전달 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 모든 경로에 CORS 적용
        return source;
    }
}
