package com.realkoreatravel.bookmark.controller;

import com.realkoreatravel.bookmark.dto.BookmarkCreateRequest;
import com.realkoreatravel.bookmark.dto.BookmarkResponse;
import com.realkoreatravel.bookmark.dto.BookmarkToggleResponse;
import com.realkoreatravel.bookmark.service.BookmarkService;
import com.realkoreatravel.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 인증된 회원의 즐겨찾기 등록·토글 endpoint를 제공한다. */
@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /** JWT 인증 주체의 회원 ID로 장소를 즐겨찾기에 등록한다. */
    @PostMapping
    public ResponseEntity<ApiResponse<BookmarkResponse>> create(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody BookmarkCreateRequest request
    ) {
        BookmarkResponse response = bookmarkService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /** JWT 인증 주체의 장소 즐겨찾기 상태를 등록·해제·복구한다. */
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<BookmarkToggleResponse>> toggle(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody BookmarkCreateRequest request
    ) {
        BookmarkToggleResponse response = bookmarkService.toggle(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
