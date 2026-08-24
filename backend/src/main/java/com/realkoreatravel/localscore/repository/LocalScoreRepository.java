package com.realkoreatravel.localscore.repository;

import com.realkoreatravel.localscore.domain.LocalScore;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Local Score 엔티티의 저장과 장소 기준 조회를 담당한다. */
public interface LocalScoreRepository extends JpaRepository<LocalScore, Long> {

    /** 장소 ID로 연결된 Local Score를 조회한다. */
    Optional<LocalScore> findByPlaceId(Long placeId);
}
