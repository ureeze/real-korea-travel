package com.realkoreatravel.bookmark.dto;

import com.realkoreatravel.bookmark.domain.Bookmark;
import lombok.Builder;

/** 즐겨찾기 토글 후 현재 상태를 반환하는 응답 DTO다. */
@Builder
public record BookmarkToggleResponse(Long bookmarkId, Long placeId, boolean bookmarked) {

    /** 즐겨찾기 Entity의 현재 활성 상태를 토글 응답으로 변환한다. */
    public static BookmarkToggleResponse from(Bookmark bookmark) {
        return BookmarkToggleResponse.builder()
                .bookmarkId(bookmark.getId())
                .placeId(bookmark.getPlace().getId())
                .bookmarked(bookmark.getDeletedAt() == null)
                .build();
    }
}
