package com.realkoreatravel.search.controller;

import com.realkoreatravel.common.response.ApiResponse;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.search.dto.SearchCondition;
import com.realkoreatravel.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /** 키워드와 선택 필터를 받아 장소 검색 결과를 반환한다. */
    @GetMapping
    public ApiResponse<PlaceListResponse> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean englishMenu,
            @RequestParam(required = false) Boolean soloFriendly,
            @RequestParam(required = false) Boolean cardAvailable,
            @RequestParam(required = false) Boolean localRecommended,
            @RequestParam(required = false) Integer maxWaitTimeMin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        SearchCondition condition = SearchCondition.builder()
                .keyword(keyword)
                .region(region)
                .category(category)
                .englishMenu(englishMenu)
                .soloFriendly(soloFriendly)
                .cardAvailable(cardAvailable)
                .localRecommended(localRecommended)
                .maxWaitTimeMin(maxWaitTimeMin)
                .page(page)
                .size(size)
                .build();
        return ApiResponse.success(searchService.search(condition));
    }
}
