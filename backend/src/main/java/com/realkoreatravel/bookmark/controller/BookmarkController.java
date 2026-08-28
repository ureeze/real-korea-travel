package com.realkoreatravel.bookmark.controller;

import com.realkoreatravel.bookmark.dto.BookmarkCreateRequest;
import com.realkoreatravel.bookmark.dto.BookmarkListResponse;
import com.realkoreatravel.bookmark.dto.BookmarkToggleResponse;
import com.realkoreatravel.bookmark.service.BookmarkService;
import com.realkoreatravel.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 인증된 회원의 즐겨찾기 목록 조회·토글 endpoint를 제공한다. */
@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /** JWT 인증 주체의 장소 즐겨찾기 상태를 등록·해제·복구한다. */
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<BookmarkToggleResponse>> toggle(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody BookmarkCreateRequest request
    ) {
        BookmarkToggleResponse response = bookmarkService.toggle(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** JWT 인증 주체의 활성 즐겨찾기를 장소 요약 정보와 함께 페이지 단위로 조회한다. */
    @GetMapping
    public ResponseEntity<ApiResponse<BookmarkListResponse>> getBookmarks(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        BookmarkListResponse response = bookmarkService.findBookmarks(memberId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
