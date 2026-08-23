package com.realkoreatravel.place.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.realkoreatravel.place.dto.PlaceDetailResponse;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.place.dto.PlaceSearchCondition;
import com.realkoreatravel.place.service.PlaceService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** 장소 목록·상세 endpoint의 HTTP 요청·응답과 Service 호출을 검증한다. */
@WebMvcTest(PlaceController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @Test
    @DisplayName("Query Parameter로 장소 목록을 조회한다")
    void getPlacesPassesQueryParametersToService() throws Exception {
        PlaceListResponse data = PlaceListResponse.builder()
                .places(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .build();
        when(placeService.findPlaces(any(PlaceSearchCondition.class))).thenReturn(data);

        mockMvc.perform(get("/api/v1/places")
                        .param("region", "seongsu")
                        .param("category", "cafe")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.page").value(0));

        verify(placeService).findPlaces(any(PlaceSearchCondition.class));
    }

    @Test
    @DisplayName("장소 ID로 상세 정보를 조회한다")
    void getPlaceReturnsDetailResponse() throws Exception {
        PlaceDetailResponse data = PlaceDetailResponse.builder()
                .id(42L)
                .name("테스트 카페")
                .address("서울시 성동구")
                .region("seongsu")
                .category("cafe")
                .build();
        when(placeService.findPlace(42L)).thenReturn(data);

        mockMvc.perform(get("/api/v1/places/{placeId}", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(42))
                .andExpect(jsonPath("$.data.name").value("테스트 카페"));

        verify(placeService).findPlace(42L);
    }
}
