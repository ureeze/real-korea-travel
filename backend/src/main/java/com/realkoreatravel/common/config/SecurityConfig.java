package com.realkoreatravel.common.config;

import com.realkoreatravel.auth.jwt.JwtAuthenticationFilter;
import com.realkoreatravel.auth.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 애플리케이션의 요청 인증 정책과 JWT 인증 필터를 하나의 Security Filter Chain으로 구성한다.
     *
     * <p>로그인·헬스 체크처럼 인증이 필요 없는 요청은 허용하고, 나머지 요청은 유효한
     * access token이 있어야 통과하도록 설정한다.</p>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtTokenProvider jwtTokenProvider
    ) throws Exception {
        http
                // 브라우저 세션을 사용하지 않는 REST API이므로 CSRF 토큰 검사를 비활성화한다.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // OAuth 로그인 진입·콜백, 토큰 API, 서비스 상태 확인 API는 인증 전에 접근할 수 있어야 한다.
                        .requestMatchers(
                                "/auth/oauth2/**",
                                "/api/v1/auth/**",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        // 위 공개 경로를 제외한 모든 API는 JWT 인증을 통과해야 한다.
                        .anyRequest().authenticated()
                )
                // 서버에 로그인 세션을 저장하지 않고 매 요청마다 JWT로 인증하는 무상태 방식을 사용한다.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        // 인증되지 않은 요청에는 401 Unauthorized를 반환한다.
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        // 인증은 되었지만 접근 권한이 없는 요청에는 403 Forbidden을 반환한다.
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpStatus.FORBIDDEN.value())
                        )
                )
                // 기본 사용자명·비밀번호 필터보다 먼저 JWT를 검증해 SecurityContext에 인증 정보를 등록한다.
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}
