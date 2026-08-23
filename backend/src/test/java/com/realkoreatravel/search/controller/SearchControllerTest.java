package com.realkoreatravel.search.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.search.dto.SearchCondition;
import com.realkoreatravel.search.service.SearchService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** 검색 endpoint의 HTTP 요청·응답과 Service 호출을 검증한다. */
@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @Test
    @DisplayName("키워드와 필터를 받아 검색 결과를 반환한다")
    void searchReturnsPlaceListResponse() throws Exception {
        PlaceListResponse data = PlaceListResponse.builder()
                .places(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .build();
        when(searchService.search(any(SearchCondition.class))).thenReturn(data);

        mockMvc.perform(get("/api/v1/search")
                        .param("keyword", "라떼")
                        .param("region", "seongsu")
                        .param("category", "cafe")
                        .param("englishMenu", "true")
                        .param("soloFriendly", "true")
                        .param("cardAvailable", "true")
                        .param("localRecommended", "true")
                        .param("maxWaitTimeMin", "15")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20));

        verify(searchService).search(any(SearchCondition.class));
    }
}
