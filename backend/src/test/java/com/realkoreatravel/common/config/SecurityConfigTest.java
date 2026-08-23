package com.realkoreatravel.common.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.realkoreatravel.auth.controller.AuthTokenController;
import com.realkoreatravel.auth.jwt.JwtTokenProvider;
import com.realkoreatravel.place.controller.PlaceController;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.place.service.PlaceService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** 공개 API와 JWT 인증이 필요한 API의 Security Filter Chain 동작을 확인하는 테스트다. */
@ActiveProfiles("test")
@WebMvcTest(controllers = {AuthTokenController.class, PlaceController.class})
@Import({SecurityConfig.class, JwtTokenProvider.class})
class SecurityConfigTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @Test
    @DisplayName("토큰 갱신 endpoint는 인증 없이 접근할 수 있다")
    void authRefreshEndpointIsPublic() throws Exception {
        // 토큰 갱신 endpoint는 인증 없이도 유효한 refresh token을 처리해야 한다.
        String refreshToken = jwtTokenProvider.issueTokens(42L).refreshToken();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("보호된 장소 endpoint는 인증 정보가 없으면 401을 반환한다")
    void protectedEndpointRequiresAuthentication() throws Exception {
        // 공개 URL이 아닌 endpoint는 인증 정보가 없으면 401을 반환해야 한다.
        mockMvc.perform(get("/api/v1/places"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효한 access token으로 보호된 장소 endpoint에 접근할 수 있다")
    void validAccessTokenPassesAuthenticationFilter() throws Exception {
        // 유효한 access token은 필터에서 인증된 요청으로 처리되어야 한다.
        String accessToken = jwtTokenProvider.issueTokens(42L).accessToken();
        when(placeService.findPlaces(any())).thenReturn(PlaceListResponse.builder()
                .places(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .build());

        mockMvc.perform(get("/api/v1/places")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("refresh token으로 보호된 장소 endpoint에 접근할 수 없다")
    void refreshTokenCannotAuthenticateProtectedEndpoint() throws Exception {
        // refresh token은 일반 API 인증에 사용할 수 없으므로 보호 endpoint에서 401이어야 한다.
        String refreshToken = jwtTokenProvider.issueTokens(42L).refreshToken();

        mockMvc.perform(get("/api/v1/places")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());
    }
}
