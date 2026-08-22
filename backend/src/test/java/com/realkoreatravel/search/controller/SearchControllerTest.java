package com.realkoreatravel.search.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.realkoreatravel.common.response.ApiResponse;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.search.dto.SearchCondition;
import com.realkoreatravel.search.service.SearchService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** 검색 endpoint가 요청 파라미터를 서비스로 전달하고 공통 응답으로 감싸는지 검증한다. */
@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @Test
    @DisplayName("키워드와 필터를 전달해 검색 결과를 반환한다")
    void searchReturnsPlaceListResponse() {
        PlaceListResponse data = PlaceListResponse.builder()
                .places(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .build();
        when(searchService.search(any(SearchCondition.class))).thenReturn(data);

        ApiResponse<PlaceListResponse> response = searchController.search(
                "라떼", "seongsu", "cafe", true, true, true, true, 15, 0, 20
        );

        assertThat(response.code()).isEqualTo("SUCCESS");
        assertThat(response.data()).isEqualTo(data);
        verify(searchService).search(any(SearchCondition.class));
    }
}
