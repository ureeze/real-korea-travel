package com.realkoreatravel.place.repository;

import com.realkoreatravel.place.domain.OpeningHour;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHourRepository extends JpaRepository<OpeningHour, Long> {

    /** 장소에 운영시간이 하나라도 등록되어 있는지 확인한다. */
    boolean existsByPlaceId(Long placeId);

    /** 장소의 운영시간을 요일과 시작 시각 순서로 조회한다. */
    List<OpeningHour> findByPlaceIdOrderByDayOfWeekAscOpenTimeAscIdAsc(Long placeId);
}
