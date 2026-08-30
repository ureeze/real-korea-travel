package com.realkoreatravel.place.service;

import com.realkoreatravel.common.config.RedisCacheConfig.CacheNames;
import com.realkoreatravel.localscore.domain.LocalScore;
import com.realkoreatravel.localscore.repository.LocalScoreRepository;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceFeature;
import com.realkoreatravel.place.domain.PlaceStatus;
import com.realkoreatravel.place.dto.PlaceDetailResponse;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.place.dto.PlaceSearchCondition;
import com.realkoreatravel.place.repository.PlaceFeatureRepository;
import com.realkoreatravel.place.repository.PlaceImageRepository;
import com.realkoreatravel.place.repository.OpeningHourRepository;
import com.realkoreatravel.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final PlaceRepository placeRepository;
    private final PlaceFeatureRepository placeFeatureRepository;
    private final PlaceImageRepository placeImageRepository;
    private final OpeningHourRepository openingHourRepository;
    private final LocalScoreRepository localScoreRepository;

    /** 필터·페이징 조건으로 활성 장소를 조회해 목록 응답으로 변환한다. */
    @Cacheable(cacheNames = CacheNames.PLACE_LIST, key = "#condition.toString()")
    public PlaceListResponse findPlaces(PlaceSearchCondition condition) {
        Pageable pageable = createPageable(condition);
        Page<Place> places = placeRepository.findActivePlaces(
                normalize(condition.region()),
                normalize(condition.category()),
                PlaceStatus.ACTIVE,
                pageable
        );
        return PlaceListResponse.from(places);
    }

    /** 활성·미삭제 장소와 편의정보·Local Score를 조회해 상세 화면용 응답으로 변환한다. */
    @Cacheable(cacheNames = CacheNames.PLACE_DETAIL, key = "#placeId")
    public PlaceDetailResponse findPlace(Long placeId) {
        Place place = placeRepository.findActivePlaceDetail(placeId, PlaceStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "장소를 찾을 수 없습니다."
        ));
        PlaceFeature feature = placeFeatureRepository.findByPlaceId(placeId).orElse(null);
        var images = placeImageRepository.findByPlaceIdOrderBySortOrderAscIdAsc(placeId);
        var openingHours = openingHourRepository.findByPlaceIdOrderByDayOfWeekAscOpenTimeAscIdAsc(placeId);
        LocalScore localScore = localScoreRepository.findByPlaceId(placeId).orElse(null);
        return PlaceDetailResponse.from(place, images, openingHours, feature, localScore);
    }

    /** 요청의 페이징·정렬 파라미터를 안전한 Spring Pageable로 변환한다. */
    private Pageable createPageable(PlaceSearchCondition condition) {
        // 음수 페이지가 들어오면 첫 번째 페이지를 조회하도록 0으로 보정한다.
        int page = Math.max(condition.page(), 0);

        // size가 없거나 0 이하이면 기본 크기를 사용하고, 한 번에 최대 100개까지만 조회한다.
        int size = condition.size() <= 0 ? DEFAULT_PAGE_SIZE : Math.min(condition.size(), MAX_PAGE_SIZE);

        // 외부 sort 문자열을 Repository가 사용할 수 있는 Spring Sort 객체로 변환한다.
        Sort sort = parseSort(condition.sort());

        // 최종적으로 페이지 번호, 페이지 크기, 정렬 조건을 하나의 Pageable로 묶는다.
        return PageRequest.of(page, size, sort);
    }

    /** 허용된 정렬 필드만 사용해 잘못된 정렬 파라미터가 내부 필드에 노출되지 않도록 한다. */
    private Sort parseSort(String sortParameter) {
        // 정렬 조건이 없으면 생성일 기준 내림차순을 기본 정렬로 사용한다.
        if (sortParameter == null || sortParameter.isBlank()) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }

        // "필드,방향" 형식으로 전달된 문자열을 최대 두 부분으로 분리한다.
        String[] values = sortParameter.split(",", 2);

        // 허용된 필드 외의 값은 기본 정렬 필드로 치환해 내부 필드 노출을 막는다.
        String sortField = switch (values[0]) {
            case "name", "createdAt", "updatedAt" -> values[0];
            default -> "createdAt";
        };

        // 방향이 없거나 잘못된 값이면 내림차순을 기본값으로 사용한다.
        Sort.Direction direction = values.length == 2
                ? Sort.Direction.fromOptionalString(values[1]).orElse(Sort.Direction.DESC)
                : Sort.Direction.DESC;

        // 검증된 정렬 필드와 방향으로 실제 정렬 객체를 생성한다.
        return Sort.by(new Sort.Order(direction, sortField));
    }

    /** 빈 문자열 필터를 null로 바꿔 선택 조건이 적용되지 않도록 한다. */
    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
