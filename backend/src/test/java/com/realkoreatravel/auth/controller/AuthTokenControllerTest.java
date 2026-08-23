package com.realkoreatravel.auth.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.realkoreatravel.auth.jwt.JwtTokenProvider;
import com.realkoreatravel.auth.jwt.JwtTokenProvider.IssuedTokens;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** refresh token 재발급 endpoint의 HTTP 응답과 오류 응답을 검증한다. */
@WebMvcTest(AuthTokenController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("유효한 refresh token으로 새 토큰 쌍을 반환한다")
    void validRefreshTokenReturnsNewTokenPair() throws Exception {
        IssuedTokens tokens = new IssuedTokens("access-token", "refresh-token", 3600L);
        when(jwtTokenProvider.getMemberIdFromRefreshToken("refresh-token")).thenReturn(42L);
        when(jwtTokenProvider.issueTokens(42L)).thenReturn(tokens);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("유효하지 않은 refresh token이면 401을 반환한다")
    void invalidRefreshTokenReturnsUnauthorized() throws Exception {
        when(jwtTokenProvider.getMemberIdFromRefreshToken("invalid-token"))
                .thenThrow(new IllegalArgumentException("유효하지 않은 JWT 토큰입니다."));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content("{\"refreshToken\":\"invalid-token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    @DisplayName("access token을 refresh token으로 사용할 수 없다")
    void accessTokenCannotBeUsedForRefresh() throws Exception {
        when(jwtTokenProvider.getMemberIdFromRefreshToken("access-token"))
                .thenThrow(new IllegalArgumentException("JWT 토큰 타입이 올바르지 않습니다."));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content("{\"refreshToken\":\"access-token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"));
    }
}
