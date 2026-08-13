package com.realkoreatravel.auth.controller;

import com.realkoreatravel.auth.dto.GoogleUserInfo;
import com.realkoreatravel.auth.dto.OAuthLoginResponse;
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

    public AuthController(GoogleOAuth2Client googleOAuth2Client, OAuth2Service oAuth2Service) {
        this.googleOAuth2Client = googleOAuth2Client;
        this.oAuth2Service = oAuth2Service;
    }

    @GetMapping("/google")
    public ResponseEntity<Void> authorize(HttpServletRequest request) {
        String redirectUri = buildRedirectUri(request);
        String authorizationUrl = googleOAuth2Client.buildAuthorizationUrl(redirectUri);
        return ResponseEntity.status(302)
                .location(java.net.URI.create(authorizationUrl))
                .build();
    }

    @GetMapping("/google/callback")
    public ApiResponse<OAuthLoginResponse> callback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request
    ) {
        String redirectUri = buildRedirectUri(request);
        GoogleUserInfo userInfo = googleOAuth2Client.fetchUserInfo(code, redirectUri);
        Member member = oAuth2Service.findOrCreateMember(userInfo);

        OAuthLoginResponse response = OAuthLoginResponse.from(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
        return ApiResponse.success(LOGIN_SUCCESS_MESSAGE, response);
    }

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
