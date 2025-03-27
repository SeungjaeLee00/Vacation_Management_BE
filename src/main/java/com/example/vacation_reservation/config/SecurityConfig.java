package com.example.vacation_reservation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()  // CSRF 보호 비활성화
                .authorizeRequests()
                .antMatchers("/api/users/register", "/api/users/send-verification-code", "/api/users/verify-code").permitAll()  // 회원가입 API는 인증 없이 접근 가능
                .anyRequest().authenticated();  // 나머지 모든 요청은 인증 필요
    }
}
