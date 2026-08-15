package com.realkoreatravel.auth.dto;

import com.realkoreatravel.auth.jwt.JwtTokenProvider.IssuedTokens;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {

    public static TokenResponse from(IssuedTokens tokens) {
        return new TokenResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                "Bearer",
                tokens.accessTokenExpiresInSeconds()
        );
    }

}
