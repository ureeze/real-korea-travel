package com.realkoreatravel.bookmark.dto;

import com.realkoreatravel.bookmark.domain.Bookmark;
import java.time.Instant;
import lombok.Builder;

/** 생성된 즐겨찾기의 식별자와 연결 정보를 반환하는 응답 DTO다. */
@Builder
public record BookmarkResponse(
        Long id,
        Long memberId,
        Long placeId,
        Instant createdAt
) {

    /** Bookmark Entity를 API 응답 DTO로 변환한다. */
    public static BookmarkResponse from(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .memberId(bookmark.getMember().getId())
                .placeId(bookmark.getPlace().getId())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}
