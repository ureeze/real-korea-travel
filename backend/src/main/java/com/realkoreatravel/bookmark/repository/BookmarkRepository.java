package com.realkoreatravel.bookmark.repository;

import com.realkoreatravel.bookmark.domain.Bookmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** 즐겨찾기의 저장과 회원·장소 조합 기준 중복 확인을 담당한다. */
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /** 삭제되지 않은 즐겨찾기를 회원과 장소의 조합으로 조회한다. */
    Optional<Bookmark> findByMemberIdAndPlaceIdAndDeletedAtIsNull(Long memberId, Long placeId);

    /** 삭제 여부와 관계없이 회원과 장소의 즐겨찾기 이력을 조회한다. */
    Optional<Bookmark> findByMemberIdAndPlaceId(Long memberId, Long placeId);

    /** 삭제되지 않은 즐겨찾기의 등록 여부를 확인한다. */
    boolean existsByMemberIdAndPlaceIdAndDeletedAtIsNull(Long memberId, Long placeId);

}
