package com.realkoreatravel.search.repository;

import com.realkoreatravel.place.domain.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 장소명·주소·설명에 대한 부분 문자열 검색과 장소 편의정보 필터를 조합해 장소를 조회한다. */
public interface SearchRepository extends JpaRepository<Place, Long> {

    /** 장소 텍스트와 지역·카테고리·편의정보 조건을 함께 적용해 검색 결과를 반환한다. */
    @Query(
            value = """
                    select p.*
                    from place p
                    join region r on r.id = p.region_id
                    join category c on c.id = p.category_id
                    left join place_feature pf on pf.place_id = p.id
                    left join local_score ls on ls.place_id = p.id
                    where p.status = :status
                      and p.deleted_at is null
                      and (p.name ilike '%' || :keyword || '%'
                           or p.address ilike '%' || :keyword || '%'
                           or p.description ilike '%' || :keyword || '%')
                      and (:region is null or r.code = :region)
                      and (:category is null or c.code = :category)
                      and (:englishMenu is null or pf.english_menu = :englishMenu)
                      and (:soloFriendly is null or pf.solo_friendly = :soloFriendly)
                      and (:cardAvailable is null or pf.card_available = :cardAvailable)
                      and (:maxWaitTimeMin is null or pf.avg_wait_time_min <= :maxWaitTimeMin)
                      and (:localRecommended is null
                           or (:localRecommended = true and ls.local_recommend_score >= 70)
                           or (:localRecommended = false and (ls.local_recommend_score is null
                                                               or ls.local_recommend_score < 70)))
                    order by p.created_at desc, p.id desc
                    """,
            countQuery = """
                    select count(*)
                    from place p
                    join region r on r.id = p.region_id
                    join category c on c.id = p.category_id
                    left join place_feature pf on pf.place_id = p.id
                    left join local_score ls on ls.place_id = p.id
                    where p.status = :status
                      and p.deleted_at is null
                      and (p.name ilike '%' || :keyword || '%'
                           or p.address ilike '%' || :keyword || '%'
                           or p.description ilike '%' || :keyword || '%')
                      and (:region is null or r.code = :region)
                      and (:category is null or c.code = :category)
                      and (:englishMenu is null or pf.english_menu = :englishMenu)
                      and (:soloFriendly is null or pf.solo_friendly = :soloFriendly)
                      and (:cardAvailable is null or pf.card_available = :cardAvailable)
                      and (:maxWaitTimeMin is null or pf.avg_wait_time_min <= :maxWaitTimeMin)
                      and (:localRecommended is null
                           or (:localRecommended = true and ls.local_recommend_score >= 70)
                           or (:localRecommended = false and (ls.local_recommend_score is null
                                                               or ls.local_recommend_score < 70)))
                    """,
            nativeQuery = true
    )
    Page<Place> searchPlaces(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("category") String category,
            @Param("englishMenu") Boolean englishMenu,
            @Param("soloFriendly") Boolean soloFriendly,
            @Param("cardAvailable") Boolean cardAvailable,
            @Param("localRecommended") Boolean localRecommended,
            @Param("maxWaitTimeMin") Integer maxWaitTimeMin,
            @Param("status") String status,
            Pageable pageable
    );
}
