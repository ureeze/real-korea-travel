package com.realkoreatravel.place.dto;

import com.realkoreatravel.place.domain.Place;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PlaceListResponse(
        List<PlaceSummary> places,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    /** 조회된 장소 페이지를 외부 응답에 사용하는 페이징 DTO로 변환한다. */
    public static PlaceListResponse from(Page<Place> placePage) {
        List<PlaceSummary> places = placePage.getContent().stream()
                .map(PlaceSummary::from)
                .toList();
        return PlaceListResponse.builder()
                .places(places)
                .page(placePage.getNumber())
                .size(placePage.getSize())
                .totalElements(placePage.getTotalElements())
                .totalPages(placePage.getTotalPages())
                .build();
    }

    @Builder
    public record PlaceSummary(
            Long id,
            String name,
            String address,
            String region,
            String category,
            BigDecimal latitude,
            BigDecimal longitude,
            Integer priceLevel
    ) {

        /** 장소 Entity에서 목록 응답에 필요한 요약 정보만 추출한다. */
        private static PlaceSummary from(Place place) {
            return PlaceSummary.builder()
                    .id(place.getId())
                    .name(place.getName())
                    .address(place.getAddress())
                    .region(place.getRegion().getCode())
                    .category(place.getCategory().getCode())
                    .latitude(place.getLatitude())
                    .longitude(place.getLongitude())
                    .priceLevel(place.getPriceLevel() == null ? null : place.getPriceLevel().intValue())
                    .build();
        }
    }
}
