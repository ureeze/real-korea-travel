package com.realkoreatravel.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.realkoreatravel.auth.dto.RefreshTokenRequest;
import com.realkoreatravel.auth.dto.TokenResponse;
import com.realkoreatravel.auth.jwt.JwtTokenProvider;
import com.realkoreatravel.auth.jwt.JwtTokenProvider.IssuedTokens;
import com.realkoreatravel.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** refresh token을 이용한 토큰 재발급 API의 정상 및 실패 응답을 확인하는 테스트다. */
class AuthTokenControllerTest {

    private JwtTokenProvider jwtTokenProvider;
    private AuthTokenController authTokenController;

    @BeforeEach
    void setUp() {
        // 웹 계층 전체를 띄우지 않고 Controller와 JWT 제공자만 직접 연결한다.
        jwtTokenProvider = new JwtTokenProvider(
                "test-secret-that-is-at-least-32-characters-long",
                900,
                1209600
        );
        authTokenController = new AuthTokenController(jwtTokenProvider);
    }

    @Test
    void validRefreshTokenReturnsNewTokenPair() {
        // 유효한 refresh token이면 새 access/refresh token 쌍을 반환해야 한다.
        IssuedTokens issuedTokens = jwtTokenProvider.issueTokens(42L);

        ResponseEntity<ApiResponse<TokenResponse>> response =
                authTokenController.refresh(new RefreshTokenRequest(issuedTokens.refreshToken()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().data().accessToken()).isNotBlank();
        assertThat(response.getBody().data().refreshToken()).isNotBlank();
        assertThat(jwtTokenProvider.getMemberIdFromAccessToken(response.getBody().data().accessToken()))
                .isEqualTo(42L);
    }

    @Test
    void invalidRefreshTokenReturnsUnauthorized() {
        // 형식과 서명이 유효하지 않은 토큰은 401과 표준 오류 코드를 반환해야 한다.
        ResponseEntity<ApiResponse<TokenResponse>> response =
                authTokenController.refresh(new RefreshTokenRequest("invalid-token"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    void accessTokenCannotBeUsedForRefresh() {
        // access token을 refresh token으로 제출해도 재발급해서는 안 된다.
        IssuedTokens issuedTokens = jwtTokenProvider.issueTokens(42L);

        ResponseEntity<ApiResponse<TokenResponse>> response =
                authTokenController.refresh(new RefreshTokenRequest(issuedTokens.accessToken()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
