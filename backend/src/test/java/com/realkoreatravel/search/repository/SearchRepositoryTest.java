package com.realkoreatravel.search.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.realkoreatravel.place.domain.Category;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceFeature;
import com.realkoreatravel.place.domain.PlaceStatus;
import com.realkoreatravel.place.domain.Region;
import com.realkoreatravel.place.repository.CategoryRepository;
import com.realkoreatravel.place.repository.PlaceFeatureRepository;
import com.realkoreatravel.place.repository.PlaceRepository;
import com.realkoreatravel.place.repository.RegionRepository;
import com.realkoreatravel.support.PostgresTestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/** PostgreSQL ILIKE 검색과 장소 편의정보·Local Score 필터를 실제 DB에서 검증한다. */
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestcontainersConfiguration.class)
class SearchRepositoryTest {

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PlaceFeatureRepository placeFeatureRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("장소명·주소·설명과 편의정보 조건으로 장소를 검색한다")
    void searchPlacesMatchesTextAndFeatureFilters() {
        Region region = saveRegion("테스트 성수", "test-seongsu");
        Category category = saveCategory("테스트 카페", "test-cafe");
        Place place = placeRepository.saveAndFlush(Place.builder()
                .region(region)
                .category(category)
                .name("성수 라떼 카페")
                .address("서울 성동구")
                .description("조용한 카페")
                .build());
        placeFeatureRepository.save(PlaceFeature.builder()
                .place(place)
                .englishMenu(true)
                .soloFriendly(true)
                .cardAvailable(true)
                .avgWaitTimeMin(10)
                .build());

        Page<Place> result = searchRepository.searchPlaces(
                "라떼",
                "test-seongsu",
                "test-cafe",
                true,
                true,
                true,
                null,
                15,
                PlaceStatus.ACTIVE.name(),
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).extracting(Place::getName)
                .containsExactly("성수 라떼 카페");
    }

    @Test
    @DisplayName("Local Score 추천 여부와 페이징 조건을 적용한다")
    void searchPlacesFiltersRecommendedPlacesAndPaginates() {
        Region region = saveRegion("추천 지역", "recommended-region");
        Category category = saveCategory("추천 카테고리", "recommended-category");
        Place recommendedPlace = savePlace(region, category, "추천 카페");
        Place ordinaryPlace = savePlace(region, category, "일반 카페");
        jdbcTemplate.update(
                "insert into local_score (place_id, local_recommend_score) values (?, ?)",
                recommendedPlace.getId(),
                85.0
        );

        Page<Place> result = searchRepository.searchPlaces(
                "카페",
                "recommended-region",
                "recommended-category",
                null,
                null,
                null,
                true,
                null,
                PlaceStatus.ACTIVE.name(),
                PageRequest.of(0, 1)
        );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).extracting(Place::getName)
                .containsExactly("추천 카페");
        assertThat(ordinaryPlace.getId()).isNotEqualTo(recommendedPlace.getId());
    }

    /** 테스트 검색 조건에 필요한 지역 데이터를 저장한다. */
    private Region saveRegion(String name, String code) {
        return regionRepository.save(Region.builder()
                .name(name)
                .code(code)
                .displayOrder(99)
                .build());
    }

    /** 테스트 검색 조건에 필요한 카테고리 데이터를 저장한다. */
    private Category saveCategory(String name, String code) {
        return categoryRepository.save(Category.builder()
                .name(name)
                .code(code)
                .displayOrder(99)
                .build());
    }

    /** 검색 결과 비교에 필요한 활성 장소를 저장한다. */
    private Place savePlace(Region region, Category category, String name) {
        return placeRepository.saveAndFlush(Place.builder()
                .region(region)
                .category(category)
                .name(name)
                .address("서울 주소")
                .description("카페 설명")
                .build());
    }
}
