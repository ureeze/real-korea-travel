package com.realkoreatravel.place.repository;

import com.realkoreatravel.place.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    /** 장소에 연결된 메뉴가 있는지 확인해 시드 중복 저장을 방지한다. */
    boolean existsByPlaceId(Long placeId);
}
