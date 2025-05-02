/**
 * Spring Security에서 사용자 인증을 처리하기 위한 {@link UserDetailsService} 구현체.
 * 사번(employeeId)을 기준으로 사용자를 조회하고, {@link CustomUserDetails} 객체로 반환.
 */
package com.example.vacation_reservation.service;

import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.repository.UserRepository;
import com.example.vacation_reservation.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 주어진 사원번호(employeeId)를 기반으로 사용자를 조회하여 {@link UserDetails} 객체로 반환.
     *
     * @param employeeId 인증에 사용할 사원번호
     * @return 사용자 정보를 담은 {@link CustomUserDetails} 객체
     * @throws UsernameNotFoundException 해당 사번에 해당하는 사용자가 존재하지 않는 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사원번호의 사용자가 없습니다."));
        return new CustomUserDetails(user);
    }
}

