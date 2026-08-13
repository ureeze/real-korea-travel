package com.realkoreatravel.auth.oauth;

import com.realkoreatravel.auth.dto.GoogleUserInfo;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

@Component
public class GoogleOAuth2Client {

    private static final String PROVIDER_REGISTRATION_ID = "google";
    private static final String AUTHORIZATION_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v3/userinfo";

    private final ClientRegistration clientRegistration;
    private final RestClient restClient;

    public GoogleOAuth2Client(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistration = clientRegistrationRepository.findByRegistrationId(PROVIDER_REGISTRATION_ID);
        this.restClient = RestClient.builder().build();
    }

    /** Google OAuth 인가 화면에 전달할 code grant 요청 URL을 구성한다. */
    public String buildAuthorizationUrl(String redirectUri) {
        return UriComponentsBuilder.fromUriString(AUTHORIZATION_URI)
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", clientRegistration.getScopes()))
                .queryParam("state", UUID.randomUUID().toString())
                .encode()
                .build()
                .toUriString();
    }

    /** 인가 코드를 access token으로 교환한 뒤 Google 사용자 정보를 조회한다. */
    public GoogleUserInfo fetchUserInfo(String authorizationCode, String redirectUri) {
        String accessToken = exchangeCodeForAccessToken(authorizationCode, redirectUri);
        return fetchUserInfo(accessToken);
    }

    /** Google token endpoint에 인가 코드를 전달해 access token을 발급받는다. */
    private String exchangeCodeForAccessToken(String authorizationCode, String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientRegistration.getClientId());
        form.add("client_secret", clientRegistration.getClientSecret());
        form.add("code", authorizationCode);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        TokenResponse tokenResponse = restClient.post()
                .uri(TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        // 토큰이 없으면 이후 사용자 정보 조회를 진행할 수 없으므로 즉시 실패시킨다.
        if (tokenResponse == null || !StringUtils.hasText(tokenResponse.accessToken())) {
            throw new IllegalStateException("Google access token을 가져오지 못했습니다.");
        }
        return tokenResponse.accessToken();
    }

    /** 발급받은 access token으로 Google 사용자 프로필을 조회한다. */
    private GoogleUserInfo fetchUserInfo(String accessToken) {
        return restClient.get()
                .uri(USER_INFO_URI)
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(GoogleUserInfo.class);
    }

    private record TokenResponse(String accessToken) {
    }
}
