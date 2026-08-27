package com.realkoreatravel.bookmark.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.realkoreatravel.bookmark.dto.BookmarkCreateRequest;
import com.realkoreatravel.bookmark.dto.BookmarkResponse;
import com.realkoreatravel.bookmark.service.BookmarkService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** 즐겨찾기 등록 endpoint의 인증 주체·요청 본문·HTTP 응답을 검증한다. */
@WebMvcTest(BookmarkController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookmarkService bookmarkService;

    @BeforeEach
    void setUp() {
        // 필터를 제외한 Web MVC 테스트에서도 @AuthenticationPrincipal에 회원 ID를 주입한다.
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(1L, null, AuthorityUtils.NO_AUTHORITIES)
        ));
    }

    @Test
    @DisplayName("인증된 회원이 장소를 즐겨찾기에 등록하면 201을 반환한다")
    void createsBookmark() throws Exception {
        BookmarkResponse response = BookmarkResponse.builder()
                .id(10L)
                .memberId(1L)
                .placeId(2L)
                .createdAt(Instant.parse("2026-08-26T00:00:00Z"))
                .build();
        when(bookmarkService.create(any(Long.class), any(BookmarkCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"placeId":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.placeId").value(2));

        verify(bookmarkService).create(eq(1L), any(BookmarkCreateRequest.class));
    }

    @Test
    @DisplayName("placeId가 없으면 400을 반환한다")
    void rejectsMissingPlaceId() throws Exception {
        mockMvc.perform(post("/api/v1/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
