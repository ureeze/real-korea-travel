package com.realkoreatravel.search.service;

import com.realkoreatravel.common.config.RedisCacheConfig.CacheNames;
import com.realkoreatravel.place.domain.PlaceStatus;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.search.dto.SearchCondition;
import com.realkoreatravel.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;

    /** 검색어와 필터를 정리해 활성 장소 검색 결과를 페이징 응답으로 변환한다. */
    @Cacheable(cacheNames = CacheNames.SEARCH_RESULT, key = "#condition.toString()")
    @Transactional(readOnly = true)
    public PlaceListResponse search(SearchCondition condition) {
        String keyword = normalizeRequiredKeyword(condition.keyword());
        String region = normalize(condition.region());
        String category = normalize(condition.category());
        PageRequest pageable = PageRequest.of(normalizePage(condition.page()), normalizeSize(condition.size()));

        return PlaceListResponse.from(searchRepository.searchPlaces(
                keyword,
                region,
                category,
                condition.englishMenu(),
                condition.soloFriendly(),
                condition.cardAvailable(),
                condition.localRecommended(),
                condition.maxWaitTimeMin(),
                PlaceStatus.ACTIVE.name(),
                pageable
        ));
    }

    /** 검색어가 비어 있으면 전체 장소 조회로 변질되지 않도록 요청을 거부한다. */
    private String normalizeRequiredKeyword(String keyword) {
        String normalized = normalize(keyword);
        if (normalized == null) {
            throw new IllegalArgumentException("검색어는 필수입니다.");
        }
        return normalized;
    }

    /** 공백 문자열을 null로 바꿔 선택 필터가 적용되지 않도록 한다. */
    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /** 음수 페이지 요청을 첫 페이지로 보정한다. */
    private int normalizePage(int page) {
        return Math.max(page, 0);
    }

    /** 페이지 크기를 1~100 범위로 보정해 과도한 조회를 막는다. */
    private int normalizeSize(int size) {
        return Math.clamp(size, 1, 100);
    }
}
