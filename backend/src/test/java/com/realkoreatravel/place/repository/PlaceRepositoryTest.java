package com.realkoreatravel.place.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.realkoreatravel.place.domain.Category;
import com.realkoreatravel.place.domain.Menu;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceFeature;
import com.realkoreatravel.place.domain.PlaceStatus;
import com.realkoreatravel.place.domain.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import com.realkoreatravel.support.PostgresTestcontainersConfiguration;

/** 지역·카테고리 필터와 활성 장소 조건이 실제 JPA 쿼리에서 동작하는지 검증하는 통합 테스트다. */
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestcontainersConfiguration.class)
class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PlaceFeatureRepository placeFeatureRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    @DisplayName("지역과 카테고리 조건에 맞는 활성 장소를 조회한다")
    void findActivePlacesFiltersByRegionAndCategory() {
        // 특정 지역과 카테고리에 속한 활성 장소를 저장해 필터링 기준을 구성한다.
        Region region = regionRepository.save(Region.builder()
                .name("테스트 지역")
                .code("test-region")
                .displayOrder(99)
                .build());
        Category category = categoryRepository.save(Category.builder()
                .name("테스트 카테고리")
                .code("test-category")
                .displayOrder(99)
                .build());
        placeRepository.save(Place.builder()
                .region(region)
                .category(category)
                .name("테스트 장소")
                .address("테스트 주소")
                .build());

        Page<Place> result = placeRepository.findActivePlaces(
                "test-region",
                "test-category",
                PlaceStatus.ACTIVE,
                PageRequest.of(0, 10)
        );

        // 두 필터를 모두 만족하는 장소만 페이지 결과에 포함되는지 검증한다.
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("테스트 장소");
    }

    @Test
    @DisplayName("종료되었거나 삭제된 장소를 조회 결과에서 제외한다")
    void findActivePlacesExcludesClosedAndDeletedPlaces() {
        // 같은 지역·카테고리에 활성, 종료, soft delete 장소를 각각 저장한다.
        Region region = regionRepository.save(Region.builder()
                .name("필터 지역")
                .code("filter-region")
                .displayOrder(98)
                .build());
        Category category = categoryRepository.save(Category.builder()
                .name("필터 카테고리")
                .code("filter-category")
                .displayOrder(98)
                .build());
        placeRepository.save(Place.builder()
                .region(region)
                .category(category)
                .name("활성 장소")
                .address("주소")
                .build());
        Place closedPlace = Place.builder()
                .region(region)
                .category(category)
                .name("종료 장소")
                .address("주소")
                .build();
        closedPlace.close();
        placeRepository.save(closedPlace);
        Place deletedPlace = Place.builder()
                .region(region)
                .category(category)
                .name("삭제 장소")
                .address("주소")
                .build();
        deletedPlace.delete();
        placeRepository.save(deletedPlace);

        Page<Place> result = placeRepository.findActivePlaces(
                "filter-region",
                "filter-category",
                PlaceStatus.ACTIVE,
                PageRequest.of(0, 10)
        );

        // Repository가 ACTIVE 상태이고 삭제되지 않은 장소만 반환하는지 검증한다.
        assertThat(result.getContent()).extracting(Place::getName)
                .containsExactly("활성 장소");
    }

    @Test
    @DisplayName("활성 장소와 편의정보·메뉴를 함께 조회한다")
    void findActivePlaceDetailFetchesRelatedData() {
        // 장소와 상세 화면에 필요한 편의정보·메뉴를 저장한다.
        Region region = regionRepository.save(Region.builder()
                .name("상세 지역")
                .code("detail-region")
                .displayOrder(97)
                .build());
        Category category = categoryRepository.save(Category.builder()
                .name("상세 카테고리")
                .code("detail-category")
                .displayOrder(97)
                .build());
        Place place = placeRepository.saveAndFlush(Place.builder()
                .region(region)
                .category(category)
                .name("상세 장소")
                .address("상세 주소")
                .build());
        placeFeatureRepository.save(PlaceFeature.builder()
                .place(place)
                .englishMenu(true)
                .cardAvailable(true)
                .soloFriendly(true)
                .reservationRequired(false)
                .parkingAvailable(false)
                .avgWaitTimeMin(15)
                .build());
        menuRepository.save(Menu.builder()
                .place(place)
                .name("추천 메뉴")
                .nameEn("Recommended Menu")
                .price(java.math.BigDecimal.valueOf(7000))
                .signature(true)
                .sortOrder(1)
                .build());

        Place result = placeRepository.findActivePlaceDetail(place.getId(), PlaceStatus.ACTIVE)
                .orElseThrow();

        // 상세 조회 결과에서 편의정보와 메뉴가 함께 초기화되는지 검증한다.
        assertThat(result.getFeature().isEnglishMenu()).isTrue();
        assertThat(result.getMenus()).extracting(Menu::getName)
                .containsExactly("추천 메뉴");
    }
}
