package com.realkoreatravel.place.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.realkoreatravel.common.response.ApiResponse;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.place.dto.PlaceSearchCondition;
import com.realkoreatravel.place.service.PlaceService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** 장소 목록 endpoint의 요청 조건 변환과 공통 응답 구조를 검증하는 단위 테스트다. */
@ExtendWith(MockitoExtension.class)
class PlaceControllerTest {

    @Mock
    private PlaceService placeService;

    @InjectMocks
    private PlaceController placeController;

    @Test
    @DisplayName("Query Parameter를 장소 조회 조건으로 변환해 Service를 호출한다")
    void getPlacesPassesQueryParametersToService() {
        // HTTP Query Parameter에 해당하는 입력값과 Service의 예상 응답을 준비한다.
        PlaceListResponse data = PlaceListResponse.builder()
                .places(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .build();
        when(placeService.findPlaces(any())).thenReturn(data);

        ApiResponse<PlaceListResponse> response = placeController.getPlaces(
                "seongsu",
                "cafe",
                0,
                20,
                "createdAt,desc"
        );

        // Controller가 성공 응답으로 Service 결과를 그대로 감싸 반환하는지 검증한다.
        assertThat(response.code()).isEqualTo("SUCCESS");
        assertThat(response.data()).isEqualTo(data);
        // Controller가 PlaceSearchCondition을 사용해 Service를 호출했는지만 검증한다.
        verify(placeService).findPlaces(any(PlaceSearchCondition.class));
    }
}
