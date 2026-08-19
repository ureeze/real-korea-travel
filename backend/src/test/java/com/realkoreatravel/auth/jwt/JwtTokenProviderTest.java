package com.realkoreatravel.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.realkoreatravel.auth.jwt.JwtTokenProvider.IssuedTokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** JWT 발급과 access/refresh token의 타입 및 서명 검증을 확인하는 테스트다. */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // 테스트마다 동일한 서명 키와 만료 시간을 사용하는 토큰 제공자를 준비한다.
        jwtTokenProvider = new JwtTokenProvider(
                "test-secret-that-is-at-least-32-characters-long",
                3600,
                1209600
        );
    }

    @Test
    void issueTokens_returnsMemberIdForAccessAndRefreshTokens() {
        // 하나의 회원에 대해 발급된 두 토큰에서 동일한 회원 ID를 읽을 수 있어야 한다.
        IssuedTokens tokens = jwtTokenProvider.issueTokens(42L);

        assertThat(jwtTokenProvider.getMemberIdFromAccessToken(tokens.accessToken())).isEqualTo(42L);
        assertThat(jwtTokenProvider.getMemberIdFromRefreshToken(tokens.refreshToken())).isEqualTo(42L);
        assertThat(tokens.accessTokenExpiresInSeconds()).isEqualTo(3600L);
    }

    @Test
    void accessTokenCannotBeUsedAsRefreshToken() {
        // 토큰 타입이 다르면 refresh token 검증에 실패해야 한다.
        IssuedTokens tokens = jwtTokenProvider.issueTokens(42L);

        assertThatThrownBy(() -> jwtTokenProvider.getMemberIdFromRefreshToken(tokens.accessToken()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refreshTokenCannotBeUsedAsAccessToken() {
        // refresh token을 일반 API 인증용 access token으로 사용할 수 없어야 한다.
        IssuedTokens tokens = jwtTokenProvider.issueTokens(42L);

        assertThatThrownBy(() -> jwtTokenProvider.getMemberIdFromAccessToken(tokens.refreshToken()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tamperedTokenIsRejected() {
        // 서명된 JWT 문자열이 변조되면 서명 검증에 실패해야 한다.
        IssuedTokens tokens = jwtTokenProvider.issueTokens(42L);
        String tamperedToken = tokens.accessToken() + "tampered";

        assertThatThrownBy(() -> jwtTokenProvider.getMemberIdFromAccessToken(tamperedToken))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
