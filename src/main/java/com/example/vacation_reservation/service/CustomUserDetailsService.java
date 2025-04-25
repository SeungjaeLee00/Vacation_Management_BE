package com.example.vacation_reservation.service;

import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.repository.UserRepository;
import com.example.vacation_reservation.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//
//    @Override
//    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
//        // 사번(employeeId)으로 사용자 조회
//        User user = userRepository.findByEmployeeId(employeeId)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with employeeId: " + employeeId));
//
//        // 권한을 빈 리스트로 반환하거나 null로 설정
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmployeeId(),
//                user.getPassword(),
//                Collections.emptyList()); // 빈 리스트로 권한을 설정
//    }

    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사원번호의 사용자가 없습니다."));
        return new CustomUserDetails(user);
    }

}

