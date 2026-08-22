package com.realkoreatravel.search.dto;

import lombok.Builder;

/** 키워드 검색과 장소 편의정보 필터를 하나의 조회 조건으로 묶는다. */
@Builder
public record SearchCondition(
        String keyword,
        String region,
        String category,
        Boolean englishMenu,
        Boolean soloFriendly,
        Boolean cardAvailable,
        Boolean localRecommended,
        Integer maxWaitTimeMin,
        int page,
        int size
) {
}
