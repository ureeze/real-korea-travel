package com.realkoreatravel.place.repository;

import com.realkoreatravel.place.domain.PlaceImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {

    /** 장소에 이미지가 하나라도 등록되어 있는지 확인한다. */
    boolean existsByPlaceId(Long placeId);

    /** 장소에 연결된 이미지를 표시 순서대로 조회한다. */
    List<PlaceImage> findByPlaceIdOrderBySortOrderAscIdAsc(Long placeId);
}
