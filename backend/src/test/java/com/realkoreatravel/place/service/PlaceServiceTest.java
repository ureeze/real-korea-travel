package com.realkoreatravel.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.realkoreatravel.localscore.domain.LocalScore;
import com.realkoreatravel.localscore.repository.LocalScoreRepository;
import com.realkoreatravel.place.domain.Category;
import com.realkoreatravel.place.domain.Menu;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceFeature;
import com.realkoreatravel.place.domain.PlaceStatus;
import com.realkoreatravel.place.domain.Region;
import com.realkoreatravel.place.dto.PlaceDetailResponse;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.place.dto.PlaceSearchCondition;
import com.realkoreatravel.place.repository.PlaceFeatureRepository;
import com.realkoreatravel.place.repository.PlaceImageRepository;
import com.realkoreatravel.place.repository.OpeningHourRepository;
import com.realkoreatravel.place.repository.PlaceRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

/** 장소 목록 조회의 필터 변환, 페이징 생성, 응답 DTO 변환을 검증하는 단위 테스트다. */
@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlaceFeatureRepository placeFeatureRepository;

    @Mock
    private PlaceImageRepository placeImageRepository;

    @Mock
    private OpeningHourRepository openingHourRepository;

    @Mock
    private LocalScoreRepository localScoreRepository;

    @InjectMocks
    private PlaceService placeService;

    @Test
    @DisplayName("필터와 페이징 조건에 따라 장소 목록 응답을 반환한다")
    void findPlacesReturnsPagedPlaceSummaries() {
        // 지역·카테고리 필터와 정렬 조건을 가진 장소 1건을 조회 결과로 준비한다.
        Region region = Region.builder()
                .name("성수")
                .code("seongsu")
                .displayOrder(1)
                .build();
        Category category = Category.builder()
                .name("카페")
                .code("cafe")
                .displayOrder(1)
                .build();
        Place place = Place.builder()
                .region(region)
                .category(category)
                .name("테스트 카페")
                .address("서울시 성동구")
                .latitude(new BigDecimal("37.5440"))
                .longitude(new BigDecimal("127.0557"))
                .priceLevel((short) 2)
                .description("테스트 장소")
                .build();
        when(placeRepository.findActivePlaces(eq("seongsu"), eq("cafe"), eq(PlaceStatus.ACTIVE), any()))
                .thenReturn(new PageImpl<>(List.of(place)));

        PlaceListResponse response = placeService.findPlaces(
                new PlaceSearchCondition("seongsu", "cafe", 1, 10, "name,asc")
        );

        // 조회 결과가 장소 요약 응답으로 변환되고 전체 건수가 유지되는지 검증한다.
        assertThat(response.places()).hasSize(1);
        assertThat(response.places().getFirst().name()).isEqualTo("테스트 카페");
        assertThat(response.places().getFirst().region()).isEqualTo("seongsu");
        assertThat(response.places().getFirst().category()).isEqualTo("cafe");
        assertThat(response.totalElements()).isEqualTo(1);

        // Service가 활성 장소 조회를 위해 Repository를 호출했는지만 검증한다.
        verify(placeRepository).findActivePlaces(eq("seongsu"), eq("cafe"), eq(PlaceStatus.ACTIVE), any());
    }

    @Test
    @DisplayName("빈 필터와 잘못된 페이징·정렬 값에 기본값을 적용한다")
    void blankFiltersUseDefaultPagingAndSort() {
        // 공백 필터와 범위를 벗어난 페이징·정렬 조건을 입력한 상황을 준비한다.
        when(placeRepository.findActivePlaces(eq(null), eq(null), eq(PlaceStatus.ACTIVE), any()))
                .thenReturn(new PageImpl<>(List.of()));

        placeService.findPlaces(new PlaceSearchCondition(" ", "", -1, 0, "unknown,sideways"));

        // 빈 필터가 전체 활성 장소 조회로 전달되는지만 검증한다.
        verify(placeRepository).findActivePlaces(eq(null), eq(null), eq(PlaceStatus.ACTIVE), any());
    }

    @Test
    @DisplayName("장소 상세 정보와 편의정보·추천 메뉴를 응답한다")
    void findPlaceReturnsDetailResponse() {
        // 상세 응답에 포함할 장소와 연관 데이터를 준비한다.
        Region region = Region.builder().name("성수").code("seongsu").displayOrder(1).build();
        Category category = Category.builder().name("카페").code("cafe").displayOrder(1).build();
        Place place = Place.builder()
                .region(region)
                .category(category)
                .name("테스트 카페")
                .address("서울시 성동구")
                .priceLevel((short) 2)
                .build();
        PlaceFeature feature = PlaceFeature.builder()
                .place(place)
                .englishMenu(true)
                .cardAvailable(true)
                .soloFriendly(true)
                .reservationRequired(false)
                .parkingAvailable(false)
                .avgWaitTimeMin(10)
                .build();
        Menu menu = Menu.builder()
                .place(place)
                .name("시그니처 라떼")
                .nameEn("Signature Latte")
                .price(new BigDecimal("6500"))
                .signature(true)
                .sortOrder(1)
                .build();
        when(placeRepository.findActivePlaceDetail(eq(42L), eq(PlaceStatus.ACTIVE)))
                .thenReturn(Optional.of(place));
        when(placeFeatureRepository.findByPlaceId(42L)).thenReturn(Optional.of(feature));
        when(placeImageRepository.findByPlaceIdOrderBySortOrderAscIdAsc(42L)).thenReturn(List.of());
        when(openingHourRepository.findByPlaceIdOrderByDayOfWeekAscOpenTimeAscIdAsc(42L)).thenReturn(List.of());
        LocalScore localScore = LocalScore.builder()
                .place(place)
                .totalScore(86)
                .foodScore(new BigDecimal("90.0"))
                .priceScore(new BigDecimal("80.0"))
                .atmosphereScore(new BigDecimal("85.0"))
                .revisitScore(new BigDecimal("88.0"))
                .localRecommendScore(new BigDecimal("85.0"))
                .build();
        when(localScoreRepository.findByPlaceId(42L)).thenReturn(Optional.of(localScore));

        PlaceDetailResponse response = placeService.findPlace(42L);

        // 장소 기본 정보와 연관 정보가 상세 응답으로 변환되는지 검증한다.
        assertThat(response.name()).isEqualTo("테스트 카페");
        assertThat(response.feature().englishMenu()).isTrue();
        assertThat(response.localScore().totalScore()).isEqualTo(86);
        assertThat(response.localScore().localRecommendScore()).isEqualByComparingTo("85.0");
        assertThat(response.recommendedMenus()).extracting(PlaceDetailResponse.MenuResponse::name)
                .containsExactly("시그니처 라떼");
        verify(placeRepository).findActivePlaceDetail(42L, PlaceStatus.ACTIVE);
        verify(placeFeatureRepository).findByPlaceId(42L);
        verify(placeImageRepository).findByPlaceIdOrderBySortOrderAscIdAsc(42L);
        verify(openingHourRepository).findByPlaceIdOrderByDayOfWeekAscOpenTimeAscIdAsc(42L);
        verify(localScoreRepository).findByPlaceId(42L);
    }

    @Test
    @DisplayName("Local Score가 없는 장소는 상세 응답의 localScore를 null로 반환한다")
    void findPlaceReturnsNullLocalScoreWhenScoreDoesNotExist() {
        // Local Score가 아직 생성되지 않은 장소의 상세 조회 상황을 준비한다.
        Region region = Region.builder().name("성수").code("seongsu").displayOrder(1).build();
        Category category = Category.builder().name("카페").code("cafe").displayOrder(1).build();
        Place place = Place.builder()
                .region(region)
                .category(category)
                .name("테스트 카페")
                .address("서울시 성동구")
                .build();
        when(placeRepository.findActivePlaceDetail(eq(42L), eq(PlaceStatus.ACTIVE)))
                .thenReturn(Optional.of(place));
        when(placeFeatureRepository.findByPlaceId(42L)).thenReturn(Optional.empty());
        when(localScoreRepository.findByPlaceId(42L)).thenReturn(Optional.empty());

        PlaceDetailResponse response = placeService.findPlace(42L);

        // 점수가 없는 장소도 상세 조회에 성공하고 localScore만 null로 반환되는지 검증한다.
        assertThat(response.localScore()).isNull();
        verify(localScoreRepository).findByPlaceId(42L);
    }
}
