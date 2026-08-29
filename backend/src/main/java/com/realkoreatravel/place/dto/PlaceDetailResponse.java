package com.realkoreatravel.place.dto;

import com.realkoreatravel.localscore.domain.LocalScore;
import com.realkoreatravel.place.domain.Menu;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceFeature;
import com.realkoreatravel.place.domain.PlaceImage;
import com.realkoreatravel.place.domain.OpeningHour;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PlaceDetailResponse(
        Long id,
        String name,
        String address,
        String region,
        String category,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer priceLevel,
        List<ImageResponse> images,
        List<OpeningHourResponse> openingHours,
        PlaceFeatureResponse feature,
        List<MenuResponse> recommendedMenus,
        LocalScoreResponse localScore
) {

    /** 장소 Entity와 별도로 조회한 편의정보·Local Score를 상세 응답으로 변환한다. */
    public static PlaceDetailResponse from(Place place, List<PlaceImage> images, List<OpeningHour> openingHours,
                                           PlaceFeature feature, LocalScore localScore) {
        return PlaceDetailResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .region(place.getRegion().getCode())
                .category(place.getCategory().getCode())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .priceLevel(place.getPriceLevel() == null ? null : place.getPriceLevel().intValue())
                .images(images.stream().map(ImageResponse::from).toList())
                .openingHours(openingHours.stream().map(OpeningHourResponse::from).toList())
                .feature(feature == null ? null : PlaceFeatureResponse.from(feature))
                .recommendedMenus(place.getMenus().stream().map(MenuResponse::from).toList())
                .localScore(localScore == null ? null : LocalScoreResponse.from(localScore))
                .build();
    }

    @Builder
    public record ImageResponse(String imageUrl, boolean main, int sortOrder) {

        /** 장소 이미지 Entity를 상세 응답용 이미지 정보로 변환한다. */
        private static ImageResponse from(PlaceImage image) {
            return ImageResponse.builder()
                    .imageUrl(image.getImageUrl())
                    .main(image.isMain())
                    .sortOrder(image.getSortOrder())
                    .build();
        }
    }

    @Builder
    public record OpeningHourResponse(short dayOfWeek, LocalTime openTime, LocalTime closeTime, boolean closed) {

        /** 운영시간 Entity를 상세 응답용 운영시간 정보로 변환한다. */
        private static OpeningHourResponse from(OpeningHour openingHour) {
            return OpeningHourResponse.builder()
                    .dayOfWeek(openingHour.getDayOfWeek())
                    .openTime(openingHour.getOpenTime())
                    .closeTime(openingHour.getCloseTime())
                    .closed(openingHour.isClosed())
                    .build();
        }
    }

    @Builder
    public record LocalScoreResponse(
            Integer totalScore,
            BigDecimal foodScore,
            BigDecimal priceScore,
            BigDecimal atmosphereScore,
            BigDecimal revisitScore,
            BigDecimal localRecommendScore
    ) {

        /** LocalScore Entity의 종합·세부 점수를 상세 응답용 DTO로 변환한다. */
        private static LocalScoreResponse from(LocalScore localScore) {
            return LocalScoreResponse.builder()
                    .totalScore(localScore.getTotalScore())
                    .foodScore(localScore.getFoodScore())
                    .priceScore(localScore.getPriceScore())
                    .atmosphereScore(localScore.getAtmosphereScore())
                    .revisitScore(localScore.getRevisitScore())
                    .localRecommendScore(localScore.getLocalRecommendScore())
                    .build();
        }
    }

    @Builder
    public record PlaceFeatureResponse(
            boolean englishMenu,
            boolean cardAvailable,
            boolean soloFriendly,
            boolean reservationRequired,
            boolean parkingAvailable,
            Integer averageWaitTimeMin
    ) {

        /** PlaceFeature Entity를 상세 응답용 편의정보로 변환한다. */
        private static PlaceFeatureResponse from(PlaceFeature feature) {
            return PlaceFeatureResponse.builder()
                    .englishMenu(feature.isEnglishMenu())
                    .cardAvailable(feature.isCardAvailable())
                    .soloFriendly(feature.isSoloFriendly())
                    .reservationRequired(feature.isReservationRequired())
                    .parkingAvailable(feature.isParkingAvailable())
                    .averageWaitTimeMin(feature.getAvgWaitTimeMin())
                    .build();
        }
    }

    @Builder
    public record MenuResponse(
            Long id,
            String name,
            String nameEn,
            BigDecimal price,
            boolean signature,
            String description,
            String imageUrl
    ) {

        /** Menu Entity를 상세 응답용 메뉴 정보로 변환한다. */
        private static MenuResponse from(Menu menu) {
            return MenuResponse.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .nameEn(menu.getNameEn())
                    .price(menu.getPrice())
                    .signature(menu.isSignature())
                    .description(menu.getDescription())
                    .imageUrl(menu.getImageUrl())
                    .build();
        }
    }
}
