package com.realkoreatravel.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /** 모든 요청에서 Bearer access token을 찾아 인증 정보를 SecurityContext에 등록한다. */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveBearerToken(request);
        if (token != null) {
            try {
                // refresh token은 일반 API 인증에 사용할 수 없도록 access token만 검증한다.
                Long memberId = jwtTokenProvider.getMemberIdFromAccessToken(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                memberId,
                                null,
                                AuthorityUtils.NO_AUTHORITIES
                        );
                // 이후 Controller와 Security가 현재 요청의 인증 사용자로 회원 ID를 사용할 수 있다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IllegalArgumentException exception) {
                // 잘못된 토큰은 인증 정보로 사용하지 않고, 보호된 API에서 401이 반환되도록 한다.
                SecurityContextHolder.clearContext();
            }
        }
        // 인증 헤더가 없거나 검증에 실패해도 다음 필터로 요청을 계속 전달한다.
        filterChain.doFilter(request, response);
    }

    /** Authorization 헤더에서 Bearer 접두사를 제거하고 JWT 문자열만 반환한다. */
    private String resolveBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
