package com.realkoreatravel.place.repository;

import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.PlaceStatus;
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
}
