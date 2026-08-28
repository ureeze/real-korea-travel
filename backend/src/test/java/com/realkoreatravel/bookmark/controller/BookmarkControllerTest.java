package com.realkoreatravel.bookmark.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.realkoreatravel.bookmark.dto.BookmarkCreateRequest;
import com.realkoreatravel.bookmark.dto.BookmarkListResponse;
import com.realkoreatravel.bookmark.dto.BookmarkToggleResponse;
import com.realkoreatravel.bookmark.service.BookmarkService;
import java.util.List;
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

/** 즐겨찾기 등록·토글 endpoint의 인증 주체·요청 본문·HTTP 응답을 검증한다. */
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
    @DisplayName("placeId가 없으면 400을 반환한다")
    void rejectsMissingPlaceId() throws Exception {
        mockMvc.perform(post("/api/v1/bookmarks/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("즐겨찾기 상태를 토글하면 200을 반환한다")
    void togglesBookmark() throws Exception {
        BookmarkToggleResponse response = BookmarkToggleResponse.builder()
                .bookmarkId(10L)
                .placeId(2L)
                .bookmarked(true)
                .build();
        when(bookmarkService.toggle(any(Long.class), any(BookmarkCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/bookmarks/toggle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"placeId":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookmarkId").value(10))
                .andExpect(jsonPath("$.data.placeId").value(2))
                .andExpect(jsonPath("$.data.bookmarked").value(true));

        verify(bookmarkService).toggle(eq(1L), any(BookmarkCreateRequest.class));
    }

    @Test
    @DisplayName("인증된 회원의 즐겨찾기 목록을 조회하면 200을 반환한다")
    void getsBookmarks() throws Exception {
        BookmarkListResponse response = BookmarkListResponse.builder()
                .bookmarks(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .build();
        when(bookmarkService.findBookmarks(1L, 0, 20)).thenReturn(response);

        mockMvc.perform(get("/api/v1/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.bookmarks").isArray())
                .andExpect(jsonPath("$.data.page").value(0));

        verify(bookmarkService).findBookmarks(1L, 0, 20);
    }
}
