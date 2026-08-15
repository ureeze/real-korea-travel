package com.realkoreatravel.auth.controller;

import com.realkoreatravel.auth.dto.GoogleUserInfo;
import com.realkoreatravel.auth.dto.TokenResponse;
import com.realkoreatravel.auth.jwt.JwtTokenProvider;
import com.realkoreatravel.auth.oauth.GoogleOAuth2Client;
import com.realkoreatravel.auth.service.OAuth2Service;
import com.realkoreatravel.common.response.ApiResponse;
import com.realkoreatravel.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/oauth2")
public class AuthController {

    private static final String LOGIN_SUCCESS_MESSAGE = "로그인 성공";

    private final GoogleOAuth2Client googleOAuth2Client;
    private final OAuth2Service oAuth2Service;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
            GoogleOAuth2Client googleOAuth2Client,
            OAuth2Service oAuth2Service,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.googleOAuth2Client = googleOAuth2Client;
        this.oAuth2Service = oAuth2Service;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /** Google 로그인 화면으로 이동할 인가 URL을 생성해 리다이렉트한다. */
    @GetMapping("/google")
    public ResponseEntity<Void> authorize(HttpServletRequest request) {
        String redirectUri = buildRedirectUri(request);
        String authorizationUrl = googleOAuth2Client.buildAuthorizationUrl(redirectUri);
        return ResponseEntity.status(302)
                .location(java.net.URI.create(authorizationUrl))
                .build();
    }

    /** Google이 전달한 인가 코드로 사용자를 확인하고 회원을 조회하거나 생성한다. */
    @GetMapping("/google/callback")
    public ApiResponse<TokenResponse> callback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request
    ) {
        String redirectUri = buildRedirectUri(request);
        // 인가 코드를 Google 사용자 정보로 교환한 뒤 애플리케이션 회원과 연결한다.
        GoogleUserInfo userInfo = googleOAuth2Client.fetchUserInfo(code, redirectUri);
        Member member = oAuth2Service.findOrCreateMember(userInfo);
        TokenResponse tokens = TokenResponse.from(jwtTokenProvider.issueTokens(member.getId()));

        return ApiResponse.success(LOGIN_SUCCESS_MESSAGE, tokens);
    }

    /** OAuth 제공자에 등록한 콜백 주소와 동일한 URI를 현재 요청 기준으로 구성한다. */
    private String buildRedirectUri(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append("://").append(serverName);
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            sb.append(':').append(serverPort);
        }
        sb.append(contextPath).append("/auth/oauth2/google/callback");
        return sb.toString();
    }
}
