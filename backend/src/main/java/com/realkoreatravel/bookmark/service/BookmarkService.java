package com.realkoreatravel.bookmark.service;

import com.realkoreatravel.bookmark.domain.Bookmark;
import com.realkoreatravel.bookmark.dto.BookmarkCreateRequest;
import com.realkoreatravel.bookmark.dto.BookmarkListResponse;
import com.realkoreatravel.bookmark.dto.BookmarkToggleResponse;
import com.realkoreatravel.bookmark.repository.BookmarkRepository;
import com.realkoreatravel.member.domain.Member;
import com.realkoreatravel.member.repository.MemberRepository;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/** 인증된 회원의 장소 즐겨찾기 등록과 토글을 담당한다. */
@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    /** 회원과 장소의 즐겨찾기 상태를 등록·해제·복구로 전환한다. */
    public BookmarkToggleResponse toggle(Long memberId, BookmarkCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "회원을 찾을 수 없습니다."
                ));
        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "장소를 찾을 수 없습니다."
                ));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByMemberIdAndPlaceId(
                memberId,
                request.placeId()
        );
        Bookmark bookmark = existingBookmark.orElseGet(
                () -> Bookmark.builder().member(member).place(place).build()
        );
        if (existingBookmark.isPresent() && bookmark.getDeletedAt() == null) {
            bookmark.softDelete();
        } else {
            bookmark.restore();
        }
        return BookmarkToggleResponse.from(bookmarkRepository.save(bookmark));
    }

    /** 회원의 활성 즐겨찾기를 최신 등록 순서의 페이지 응답으로 변환한다. */
    public BookmarkListResponse findBookmarks(Long memberId, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size <= 0 ? 20 : Math.min(size, 100);
        Page<Bookmark> bookmarks = bookmarkRepository.findActiveBookmarks(
                memberId,
                PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Order.desc("createdAt")))
        );
        return BookmarkListResponse.from(bookmarks);
    }
}
