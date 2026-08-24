package com.realkoreatravel.localscore.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.realkoreatravel.localscore.domain.LocalScore;
import com.realkoreatravel.place.domain.Category;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.Region;
import com.realkoreatravel.place.repository.CategoryRepository;
import com.realkoreatravel.place.repository.PlaceRepository;
import com.realkoreatravel.place.repository.RegionRepository;
import com.realkoreatravel.support.PostgresTestcontainersConfiguration;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

/** Local Score의 JPA 매핑과 Place 1:1 제약을 실제 PostgreSQL에서 검증한다. */
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestcontainersConfiguration.class)
class LocalScoreRepositoryTest {

    @Autowired
    private LocalScoreRepository localScoreRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("장소 ID로 Local Score를 조회한다")
    void findByPlaceIdReturnsLocalScore() {
        // Local Score가 연결될 장소를 먼저 저장한다.
        Place place = savePlace("score-place", "score-category");
        LocalScore expected = localScoreRepository.saveAndFlush(LocalScore.builder()
                .place(place)
                .totalScore(85)
                .foodScore(BigDecimal.valueOf(90.0))
                .priceScore(BigDecimal.valueOf(80.0))
                .atmosphereScore(BigDecimal.valueOf(85.0))
                .revisitScore(BigDecimal.valueOf(88.0))
                .localRecommendScore(BigDecimal.valueOf(85.0))
                .build());

        LocalScore actual = localScoreRepository.findByPlaceId(place.getId()).orElseThrow();

        // 저장한 점수와 장소 관계가 조회 결과에 그대로 복원되는지 검증한다.
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getPlace().getId()).isEqualTo(place.getId());
        assertThat(actual.getTotalScore()).isEqualTo(85);
        assertThat(actual.getLocalRecommendScore()).isEqualByComparingTo("85.0");
    }

    @Test
    @DisplayName("한 장소에 Local Score를 중복 저장할 수 없다")
    void duplicateLocalScoreForPlaceViolatesUniqueConstraint() {
        // 하나의 장소에 첫 번째 Local Score를 저장한다.
        Place place = savePlace("duplicate-place", "duplicate-category");
        localScoreRepository.saveAndFlush(LocalScore.builder()
                .place(place)
                .totalScore(80)
                .build());

        // 같은 장소에 두 번째 점수를 저장하면 place_id UNIQUE 제약에 걸려야 한다.
        LocalScore duplicate = LocalScore.builder()
                .place(place)
                .totalScore(70)
                .build();

        assertThatThrownBy(() -> localScoreRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    /** Repository 테스트에서 사용할 지역·카테고리·장소 데이터를 생성한다. */
    private Place savePlace(String regionCode, String categoryCode) {
        Region region = regionRepository.save(Region.builder()
                .name(regionCode)
                .code(regionCode)
                .displayOrder(99)
                .build());
        Category category = categoryRepository.save(Category.builder()
                .name(categoryCode)
                .code(categoryCode)
                .displayOrder(99)
                .build());
        return placeRepository.saveAndFlush(Place.builder()
                .region(region)
                .category(category)
                .name("테스트 장소")
                .address("테스트 주소")
                .build());
    }
}
