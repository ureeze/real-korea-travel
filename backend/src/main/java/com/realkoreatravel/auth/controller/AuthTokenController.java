package com.realkoreatravel.auth.controller;

import com.realkoreatravel.auth.dto.RefreshTokenRequest;
import com.realkoreatravel.auth.dto.TokenResponse;
import com.realkoreatravel.auth.jwt.JwtTokenProvider;
import com.realkoreatravel.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        try {
            Long memberId = jwtTokenProvider.getMemberIdFromRefreshToken(request.refreshToken());
            TokenResponse tokens = TokenResponse.from(jwtTokenProvider.issueTokens(memberId));
            return ResponseEntity.ok(ApiResponse.success(tokens));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("INVALID_REFRESH_TOKEN", "유효하지 않은 refresh token입니다."));
        }
    }
}
