package com.realkoreatravel.place.dto;

public record PlaceSearchCondition(
        String region,
        String category,
        int page,
        int size,
        String sort
) {
}
