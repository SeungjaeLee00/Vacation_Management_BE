/**
 * JWT 기반 인증을 수행하는 필터 클래스
 * <p>
 * 매 요청마다 실행되며, HTTP 요청 헤더에서 JWT 토큰을 추출하고 유효성을 검증한 후,
 * 해당 토큰에 포함된 사용자 정보를 기반으로 Spring Security의 인증 컨텍스트를 설정함.
 * </p>
 */

package com.example.vacation_reservation.security;

import com.example.vacation_reservation.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 실제 필터 동작을 수행하는 메서드
     * JWT 토큰을 요청에서 추출하고, 유효할 경우 SecurityContext에 인증 정보를 설정함
     *
     * @param request     HTTP 요청
     * @param response    HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 필터 처리 중 예외
     * @throws IOException      입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 요청에서 JWT 토큰 추출
            String token = getJwtFromRequest(request);

            // 토큰이 있고, 유효하면
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 토큰에서 사번(employeeId) 추출
                String employeeId = jwtTokenProvider.getEmployeeIdFromToken(token);

                // 사번을 기반으로 사용자 정보 조회
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(employeeId);

                // 권한을 빈 리스트로 설정하여 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());  // 빈 리스트로 권한 설정

                // 요청에 대한 추가 정보를 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Security Context에 사용자 인증을 설정할 수 없습니다.", ex);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 Authorization 헤더를 추출하여 JWT 토큰을 반환함.
     *"Bearer " 있을 경우 이를 제거한 토큰 문자열을 반환
     *
     * @param request HTTP 요청
     * @return JWT 토큰 문자열 (없을 경우 null)
     */
    public static String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer "를 제외하고 토큰만 반환
        }

        if (request.getCookies() != null) {
            for (javax.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
