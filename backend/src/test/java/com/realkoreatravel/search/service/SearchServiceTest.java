package com.realkoreatravel.search.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.realkoreatravel.search.dto.SearchCondition;
import com.realkoreatravel.search.repository.SearchRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

/** 검색어 정리, 필터 전달, 페이징 보정 등 검색 서비스 동작을 검증한다. */
@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchService searchService;

    @Test
    @DisplayName("검색어와 편의정보 필터를 Repository에 전달한다")
    void searchPassesKeywordAndFiltersToRepository() {
        when(searchRepository.searchPlaces(
                eq("라떼"),
                eq("seongsu"),
                eq("cafe"),
                eq(true),
                eq(true),
                eq(true),
                eq(true),
                eq(15),
                eq("ACTIVE"),
                any()
        )).thenReturn(new PageImpl<>(List.of()));

        searchService.search(SearchCondition.builder()
                .keyword(" 라떼 ")
                .region(" seongsu ")
                .category("cafe")
                .englishMenu(true)
                .soloFriendly(true)
                .cardAvailable(true)
                .localRecommended(true)
                .maxWaitTimeMin(15)
                .page(0)
                .size(20)
                .build());

        verify(searchRepository).searchPlaces(
                eq("라떼"),
                eq("seongsu"),
                eq("cafe"),
                eq(true),
                eq(true),
                eq(true),
                eq(true),
                eq(15),
                eq("ACTIVE"),
                any()
        );
    }

    @Test
    @DisplayName("빈 검색어 요청을 거부한다")
    void blankKeywordIsRejected() {
        assertThatThrownBy(() -> searchService.search(
                SearchCondition.builder()
                        .keyword(" ")
                        .page(0)
                        .size(20)
                        .build()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색어는 필수입니다.");
    }
}
