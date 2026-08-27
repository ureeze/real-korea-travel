package com.realkoreatravel.bookmark.service;

import com.realkoreatravel.bookmark.domain.Bookmark;
import com.realkoreatravel.bookmark.dto.BookmarkCreateRequest;
import com.realkoreatravel.bookmark.dto.BookmarkResponse;
import com.realkoreatravel.bookmark.dto.BookmarkToggleResponse;
import com.realkoreatravel.bookmark.repository.BookmarkRepository;
import com.realkoreatravel.member.domain.Member;
import com.realkoreatravel.member.repository.MemberRepository;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.repository.PlaceRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** 인증된 회원의 장소 즐겨찾기 등록과 토글을 담당한다. */
@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    /** 회원과 장소의 유효성을 확인하고 중복 없이 즐겨찾기를 저장한다. */
    public BookmarkResponse create(Long memberId, BookmarkCreateRequest request) {
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
        if (bookmarkRepository.existsByMemberIdAndPlaceIdAndDeletedAtIsNull(memberId, request.placeId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 즐겨찾기에 등록된 장소입니다."
            );
        }

        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .place(place)
                .build();
        return BookmarkResponse.from(bookmarkRepository.save(bookmark));
    }

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
}
