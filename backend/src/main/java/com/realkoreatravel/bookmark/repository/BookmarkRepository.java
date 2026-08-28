package com.realkoreatravel.bookmark.repository;

import com.realkoreatravel.bookmark.domain.Bookmark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 즐겨찾기의 저장과 회원·장소 조합 기준 중복 확인을 담당한다. */
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /** 삭제되지 않은 즐겨찾기를 회원과 장소의 조합으로 조회한다. */
    Optional<Bookmark> findByMemberIdAndPlaceIdAndDeletedAtIsNull(Long memberId, Long placeId);

    /** 삭제 여부와 관계없이 회원과 장소의 즐겨찾기 이력을 조회한다. */
    Optional<Bookmark> findByMemberIdAndPlaceId(Long memberId, Long placeId);

    /** 회원의 활성 즐겨찾기와 장소 요약에 필요한 지역·카테고리를 함께 페이지 조회한다. */
    @Query(value = """
            select b from Bookmark b
            join fetch b.place place
            join fetch place.region
            join fetch place.category
            where b.member.id = :memberId
              and b.deletedAt is null
            """,
            countQuery = """
            select count(b) from Bookmark b
            where b.member.id = :memberId
              and b.deletedAt is null
            """)
    Page<Bookmark> findActiveBookmarks(@Param("memberId") Long memberId, Pageable pageable);

}
