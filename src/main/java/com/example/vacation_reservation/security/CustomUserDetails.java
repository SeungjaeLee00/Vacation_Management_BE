package com.example.vacation_reservation.security;

import com.example.vacation_reservation.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    // 사번 가져오기
    public String getEmployeeId() {
        return user.getEmployeeId();
    }

    // 이름 가져오기
    public String getName() {
        return user.getName();
    }

    // 이메일 가져오기
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 이게 뭐지. 권한 안 쓰면 이대로 둬도 OK
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmployeeId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
