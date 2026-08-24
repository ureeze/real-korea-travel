package com.realkoreatravel.place.repository;

import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(
            value = """
                    select p from Place p
                    join fetch p.region region
                    join fetch p.category category
                    where p.status = :status
                      and p.deletedAt is null
                      and (:regionCode is null or region.code = :regionCode)
                      and (:categoryCode is null or category.code = :categoryCode)
                    """,
            countQuery = """
                    select count(p) from Place p
                    join p.region region
                    join p.category category
                    where p.status = :status
                      and p.deletedAt is null
                      and (:regionCode is null or region.code = :regionCode)
                      and (:categoryCode is null or category.code = :categoryCode)
                    """
    )
    Page<Place> findActivePlaces(
            @Param("regionCode") String regionCode,
            @Param("categoryCode") String categoryCode,
            @Param("status") PlaceStatus status,
            Pageable pageable
    );

    /** 활성·미삭제 장소와 상세 화면에 필요한 장소 기본 정보·메뉴를 함께 조회한다. */
    @Query("""
            select distinct p from Place p
            join fetch p.region
            join fetch p.category
            left join fetch p.menus
            where p.id = :placeId
              and p.status = :status
              and p.deletedAt is null
            """)
    Optional<Place> findActivePlaceDetail(
            @Param("placeId") Long placeId,
            @Param("status") PlaceStatus status
    );
}
