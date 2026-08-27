package com.realkoreatravel.bookmark.dto;

import jakarta.validation.constraints.NotNull;

/** 즐겨찾기에 등록할 장소 ID를 전달하는 요청 DTO다. */
public record BookmarkCreateRequest(
        @NotNull(message = "placeId는 필수입니다.")
        Long placeId
) {
}
