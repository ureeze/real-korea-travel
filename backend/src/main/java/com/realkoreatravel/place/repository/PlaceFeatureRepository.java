package com.realkoreatravel.place.repository;

import com.realkoreatravel.place.domain.PlaceFeature;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceFeatureRepository extends JpaRepository<PlaceFeature, Long> {

    /** 장소 ID로 연결된 편의정보를 조회한다. */
    Optional<PlaceFeature> findByPlaceId(Long placeId);
}
