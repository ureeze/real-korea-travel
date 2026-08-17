package com.realkoreatravel.common.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.realkoreatravel.auth.controller.AuthTokenController;
import com.realkoreatravel.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** 공개 API와 JWT 인증이 필요한 API의 Security Filter Chain 동작을 확인하는 테스트다. */
@ActiveProfiles("test")
@WebMvcTest(controllers = AuthTokenController.class)
@Import({SecurityConfig.class, JwtTokenProvider.class})
class SecurityConfigTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authRefreshEndpointIsPublic() throws Exception {
        // 토큰 갱신 endpoint는 인증 없이도 유효한 refresh token을 처리해야 한다.
        String refreshToken = jwtTokenProvider.issueTokens(42L).refreshToken();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        // 공개 URL이 아닌 endpoint는 인증 정보가 없으면 401을 반환해야 한다.
        mockMvc.perform(get("/api/v1/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validAccessTokenPassesAuthenticationFilter() throws Exception {
        // 유효한 access token은 필터에서 인증된 요청으로 처리되어야 한다.
        String accessToken = jwtTokenProvider.issueTokens(42L).accessToken();

        mockMvc.perform(get("/api/v1/protected")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void refreshTokenCannotAuthenticateProtectedEndpoint() throws Exception {
        // refresh token은 일반 API 인증에 사용할 수 없으므로 보호 endpoint에서 401이어야 한다.
        String refreshToken = jwtTokenProvider.issueTokens(42L).refreshToken();

        mockMvc.perform(get("/api/v1/protected")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());
    }
}
