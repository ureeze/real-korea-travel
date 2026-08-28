package com.realkoreatravel.bookmark.dto;

import com.realkoreatravel.bookmark.domain.Bookmark;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

/** 활성 즐겨찾기와 연결된 장소 요약 정보를 페이지 형태로 반환한다. */
@Builder
public record BookmarkListResponse(
        List<BookmarkSummary> bookmarks,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    /** Repository 페이지 결과를 API 응답용 페이징 DTO로 변환한다. */
    public static BookmarkListResponse from(Page<Bookmark> bookmarkPage) {
        return BookmarkListResponse.builder()
                .bookmarks(bookmarkPage.getContent().stream().map(BookmarkSummary::from).toList())
                .page(bookmarkPage.getNumber())
                .size(bookmarkPage.getSize())
                .totalElements(bookmarkPage.getTotalElements())
                .totalPages(bookmarkPage.getTotalPages())
                .build();
    }

    /** 즐겨찾기 식별자와 화면 목록에 필요한 장소 요약 정보를 담는다. */
    @Builder
    public record BookmarkSummary(
            Long bookmarkId,
            Long placeId,
            String name,
            String address,
            String region,
            String category,
            BigDecimal latitude,
            BigDecimal longitude,
            Integer priceLevel,
            Instant bookmarkedAt
    ) {

        /** 즐겨찾기 Entity에서 회원 정보는 제외하고 장소 요약 응답으로 변환한다. */
        private static BookmarkSummary from(Bookmark bookmark) {
            var place = bookmark.getPlace();
            return BookmarkSummary.builder()
                    .bookmarkId(bookmark.getId())
                    .placeId(place.getId())
                    .name(place.getName())
                    .address(place.getAddress())
                    .region(place.getRegion().getCode())
                    .category(place.getCategory().getCode())
                    .latitude(place.getLatitude())
                    .longitude(place.getLongitude())
                    .priceLevel(place.getPriceLevel() == null ? null : place.getPriceLevel().intValue())
                    .bookmarkedAt(bookmark.getCreatedAt())
                    .build();
        }
    }
}
