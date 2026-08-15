package com.realkoreatravel.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private final SecretKey signingKey;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;

    /** 환경변수로 주입된 secret과 토큰별 만료 시간을 JWT 발급 설정으로 초기화한다. */
    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds,
            @Value("${security.jwt.refresh-token-expiration-seconds}") long refreshTokenExpirationSeconds
    ) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret은 32자 이상이어야 합니다.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }

    /** 하나의 회원에 대해 access token과 refresh token을 함께 발급한다. */
    public IssuedTokens issueTokens(Long memberId) {
        Instant now = Instant.now();
        String accessToken = issueToken(memberId, ACCESS_TOKEN, now, accessTokenExpirationSeconds);
        String refreshToken = issueToken(memberId, REFRESH_TOKEN, now, refreshTokenExpirationSeconds);
        return new IssuedTokens(accessToken, refreshToken, accessTokenExpirationSeconds);
    }

    /** access token을 검증하고 토큰 subject에 저장된 회원 ID를 반환한다. */
    public Long getMemberIdFromAccessToken(String token) {
        return getMemberId(token, ACCESS_TOKEN);
    }

    /** refresh token을 검증하고 토큰 subject에 저장된 회원 ID를 반환한다. */
    public Long getMemberIdFromRefreshToken(String token) {
        return getMemberId(token, REFRESH_TOKEN);
    }

    /** 회원 ID와 토큰 종류를 claims에 저장하고 서명된 JWT 문자열을 생성한다. */
    private String issueToken(Long memberId, String tokenType, Instant issuedAt, long expirationSeconds) {
        Instant expiration = issuedAt.plusSeconds(expirationSeconds);
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    /** 서명·만료·토큰 종류를 검증한 뒤 subject를 회원 ID로 변환한다. */
    private Long getMemberId(String token, String expectedTokenType) {
        try {
            // signingKey 검증이 통과해야만 신뢰할 수 있는 claims를 읽을 수 있다.
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!expectedTokenType.equals(claims.get(TOKEN_TYPE_CLAIM, String.class))) {
                throw new IllegalArgumentException("JWT 토큰 타입이 올바르지 않습니다.");
            }
            // access token은 필터에서, refresh token은 재발급 API에서 각각 사용한다.
            return Long.valueOf(claims.getSubject());
        } catch (JwtException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.", exception);
        }
    }

    public record IssuedTokens(
            String accessToken,
            String refreshToken,
            long accessTokenExpiresInSeconds
    ) {
        // access token 만료 시간만 클라이언트 응답에 함께 전달한다.
    }
}
