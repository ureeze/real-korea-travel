package com.realkoreatravel.place.controller;

import com.realkoreatravel.common.response.ApiResponse;
import com.realkoreatravel.place.dto.PlaceDetailResponse;
import com.realkoreatravel.place.dto.PlaceListResponse;
import com.realkoreatravel.place.dto.PlaceSearchCondition;
import com.realkoreatravel.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    /** 지역·카테고리 조건과 페이징 조건으로 장소 목록을 조회한다. */
    @GetMapping
    public ApiResponse<PlaceListResponse> getPlaces(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        PlaceSearchCondition condition = new PlaceSearchCondition(region, category, page, size, sort);
        return ApiResponse.success(placeService.findPlaces(condition));
    }

    /** 장소 ID로 활성 장소의 기본 정보와 상세 연관 정보를 조회한다. */
    @GetMapping("/{placeId}")
    public ApiResponse<PlaceDetailResponse> getPlace(@PathVariable Long placeId) {
        return ApiResponse.success(placeService.findPlace(placeId));
    }
}