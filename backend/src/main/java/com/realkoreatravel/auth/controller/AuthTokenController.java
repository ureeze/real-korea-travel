package com.realkoreatravel.auth.controller;

import com.realkoreatravel.auth.dto.RefreshTokenRequest;
import com.realkoreatravel.auth.dto.TokenResponse;
import com.realkoreatravel.auth.jwt.JwtTokenProvider;
import com.realkoreatravel.common.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthTokenController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthTokenController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /** 유효한 refresh token으로 새로운 access token과 refresh token을 발급한다. */
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        Long memberId = jwtTokenProvider.getMemberIdFromRefreshToken(request.refreshToken());
        TokenResponse tokens = TokenResponse.from(jwtTokenProvider.issueTokens(memberId));
        return ApiResponse.success(tokens);
    }
}
