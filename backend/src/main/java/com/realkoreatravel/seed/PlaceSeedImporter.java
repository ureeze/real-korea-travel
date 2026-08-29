package com.realkoreatravel.seed;

import com.realkoreatravel.localscore.domain.LocalScore;
import com.realkoreatravel.localscore.repository.LocalScoreRepository;
import com.realkoreatravel.place.domain.Category;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceFeature;
import com.realkoreatravel.place.domain.PlaceImage;
import com.realkoreatravel.place.domain.OpeningHour;
import com.realkoreatravel.place.domain.Menu;
import com.realkoreatravel.place.domain.Region;
import com.realkoreatravel.place.repository.CategoryRepository;
import com.realkoreatravel.place.repository.PlaceFeatureRepository;
import com.realkoreatravel.place.repository.MenuRepository;
import com.realkoreatravel.place.repository.PlaceImageRepository;
import com.realkoreatravel.place.repository.OpeningHourRepository;
import com.realkoreatravel.place.repository.PlaceRepository;
import com.realkoreatravel.place.repository.RegionRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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
    private final MenuRepository menuRepository;
    private final PlaceImageRepository placeImageRepository;
    private final OpeningHourRepository openingHourRepository;
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;

    /** 애플리케이션 시작 후 CSV를 읽어 아직 없는 장소만 저장한다. */
    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) {
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

    /** 외부 장소 ID를 기준으로 장소를 식별하고, 신규 장소 또는 누락된 상세 데이터를 저장한다. */
    private void saveIfAbsent(PlaceSeedRow row) {
        // google_place_id를 기준으로 이미 적재된 장소인지 확인해 장소 자체의 중복 생성을 막는다.
        Place existingPlace = placeRepository.findByGooglePlaceId(row.googlePlaceId()).orElse(null);
        if (existingPlace != null) {
            // 기존 장소도 이미지·메뉴·운영시간이 누락되었을 수 있으므로 상세 데이터를 보강한다.
            saveDefaultContent(existingPlace);
            return;
        }

        // CSV의 지역·카테고리 코드를 실제 연관 Entity로 변환하고, 코드가 없으면 시드 적재를 중단한다.
        Region region = regionRepository.findByCode(row.regionCode())
                .orElseThrow(() -> new IllegalStateException("지역 코드를 찾을 수 없습니다: " + row.regionCode()));
        Category category = categoryRepository.findByCode(row.categoryCode())
                .orElseThrow(() -> new IllegalStateException("카테고리 코드를 찾을 수 없습니다: " + row.categoryCode()));

        // CSV 한 행의 장소 기본 정보를 Entity로 만들어 저장한다.
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

        // 신규 장소에 검색·상세 화면에서 사용할 기본 편의정보를 생성한다.
        placeFeatureRepository.save(PlaceFeature.builder()
                .place(place)
                .cardAvailable(true)
                .soloFriendly(true)
                .avgWaitTimeMin(0)
                .build());

        // 신규 장소에 이미지·대표 메뉴·요일별 운영시간을 함께 생성한다.
        saveDefaultContent(place);

        // 신규 장소의 초기 Local Score를 생성해 점수 기반 조회가 가능하도록 한다.
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

    /** 시드 장소에 상세 화면에서 사용할 이미지·메뉴·운영시간 기본 데이터를 추가한다. */
    private void saveDefaultContent(Place place) {
        if (!placeImageRepository.existsByPlaceId(place.getId())) {
            placeImageRepository.save(PlaceImage.builder()
                    .place(place)
                    .imageUrl("https://placehold.co/1200x800?text=" + place.getName())
                    .main(true)
                    .sortOrder(1)
                    .build());
        }
        if (!menuRepository.existsByPlaceId(place.getId())) {
            menuRepository.save(Menu.builder()
                    .place(place)
                    .name(place.getName() + " 대표 메뉴")
                    .nameEn("Signature Menu")
                    .price(BigDecimal.valueOf(10000))
                    .signature(true)
                    .description("RKT MVP 시드 대표 메뉴")
                    .sortOrder(1)
                    .build());
        }
        if (!openingHourRepository.existsByPlaceId(place.getId())) {
            for (short day = 1; day <= 7; day++) {
                openingHourRepository.save(OpeningHour.builder()
                        .place(place)
                        .dayOfWeek(day)
                        .openTime(LocalTime.of(11, 0))
                        .closeTime(LocalTime.of(22, 0))
                        .closed(false)
                        .build());
            }
        }
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
