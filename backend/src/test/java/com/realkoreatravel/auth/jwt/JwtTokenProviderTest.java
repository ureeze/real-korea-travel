package com.realkoreatravel.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.realkoreatravel.auth.jwt.JwtTokenProvider.IssuedTokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "test-secret-that-is-at-least-32-characters-long",
                900,
                1209600
        );
    }

    @Test
    void issueTokens_returnsMemberIdForAccessAndRefreshTokens() {
        IssuedTokens tokens = jwtTokenProvider.issueTokens(42L);

        assertThat(jwtTokenProvider.getMemberIdFromAccessToken(tokens.accessToken())).isEqualTo(42L);
        assertThat(jwtTokenProvider.getMemberIdFromRefreshToken(tokens.refreshToken())).isEqualTo(42L);
        assertThat(tokens.accessTokenExpiresInSeconds()).isEqualTo(900L);
    }

    @Test
    void accessTokenCannotBeUsedAsRefreshToken() {
        IssuedTokens tokens = jwtTokenProvider.issueTokens(42L);

        assertThatThrownBy(() -> jwtTokenProvider.getMemberIdFromRefreshToken(tokens.accessToken()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refreshTokenCannotBeUsedAsAccessToken() {
        IssuedTokens tokens = jwtTokenProvider.issueTokens(42L);

        assertThatThrownBy(() -> jwtTokenProvider.getMemberIdFromAccessToken(tokens.refreshToken()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tamperedTokenIsRejected() {
        IssuedTokens tokens = jwtTokenProvider.issueTokens(42L);
        String tamperedToken = tokens.accessToken() + "tampered";

        assertThatThrownBy(() -> jwtTokenProvider.getMemberIdFromAccessToken(tamperedToken))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
