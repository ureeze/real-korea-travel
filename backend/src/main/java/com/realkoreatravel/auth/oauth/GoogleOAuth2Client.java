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

    public GoogleUserInfo fetchUserInfo(String authorizationCode, String redirectUri) {
        String accessToken = exchangeCodeForAccessToken(authorizationCode, redirectUri);
        return fetchUserInfo(accessToken);
    }

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

        if (tokenResponse == null || !StringUtils.hasText(tokenResponse.accessToken())) {
            throw new IllegalStateException("Google access token을 가져오지 못했습니다.");
        }
        return tokenResponse.accessToken();
    }

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
