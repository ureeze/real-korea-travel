package com.realkoreatravel.seed;

import com.realkoreatravel.localscore.domain.LocalScore;
import com.realkoreatravel.localscore.repository.LocalScoreRepository;
import com.realkoreatravel.place.domain.Category;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceFeature;
import com.realkoreatravel.place.domain.Region;
import com.realkoreatravel.place.repository.CategoryRepository;
import com.realkoreatravel.place.repository.PlaceFeatureRepository;
import com.realkoreatravel.place.repository.PlaceRepository;
import com.realkoreatravel.place.repository.RegionRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** seed 프로필에서 CSV 장소 데이터를 읽어 개발용 장소와 기본 상세 데이터를 적재한다. */
@Component
@Profile("seed")
@RequiredArgsConstructor
public class PlaceSeedImporter implements ApplicationRunner {

    private static final String CSV_PATH = "seed/places.csv";

    private final CategoryRepository categoryRepository;
    private final LocalScoreRepository localScoreRepository;
    private final PlaceFeatureRepository placeFeatureRepository;
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;

    /** 애플리케이션 시작 후 CSV를 읽어 아직 없는 장소만 저장한다. */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                saveIfAbsent(PlaceSeedRow.parse(line));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("장소 시드 CSV를 읽을 수 없습니다.", exception);
        }
    }

    /** 외부 장소 ID를 기준으로 중복을 건너뛰고 장소와 기본 상세 데이터를 저장한다. */
    private void saveIfAbsent(PlaceSeedRow row) {
        if (placeRepository.findByGooglePlaceId(row.googlePlaceId()).isPresent()) {
            return;
        }
        Region region = regionRepository.findByCode(row.regionCode())
                .orElseThrow(() -> new IllegalStateException("지역 코드를 찾을 수 없습니다: " + row.regionCode()));
        Category category = categoryRepository.findByCode(row.categoryCode())
                .orElseThrow(() -> new IllegalStateException("카테고리 코드를 찾을 수 없습니다: " + row.categoryCode()));
        Place place = placeRepository.save(Place.builder()
                .region(region)
                .category(category)
                .name(row.name())
                .address(row.address())
                .latitude(row.latitude())
                .longitude(row.longitude())
                .priceLevel(row.priceLevel())
                .description(row.description())
                .googlePlaceId(row.googlePlaceId())
                .build());
        placeFeatureRepository.save(PlaceFeature.builder()
                .place(place)
                .cardAvailable(true)
                .soloFriendly(true)
                .avgWaitTimeMin(0)
                .build());
        localScoreRepository.save(LocalScore.builder()
                .place(place)
                .totalScore(70)
                .foodScore(BigDecimal.valueOf(70.0))
                .priceScore(BigDecimal.valueOf(70.0))
                .atmosphereScore(BigDecimal.valueOf(70.0))
                .revisitScore(BigDecimal.valueOf(70.0))
                .localRecommendScore(BigDecimal.valueOf(70.0))
                .build());
    }

    /** CSV 한 줄을 장소 적재에 필요한 타입으로 변환한다. */
    private record PlaceSeedRow(
            String regionCode,
            String categoryCode,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            Short priceLevel,
            String description,
            String googlePlaceId
    ) {

        /** 쉼표로 구분된 CSV 한 줄을 검증하고 도메인 입력 값으로 변환한다. */
        private static PlaceSeedRow parse(String line) {
            String[] values = line.split(",", -1);
            if (values.length != 9) {
                throw new IllegalArgumentException("장소 시드 CSV 컬럼 수가 올바르지 않습니다: " + line);
            }
            return new PlaceSeedRow(
                    values[0], values[1], values[2], values[3],
                    new BigDecimal(values[4]), new BigDecimal(values[5]),
                    Short.valueOf(values[6]), values[7], values[8]
            );
        }
    }
}
