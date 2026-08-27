package com.realkoreatravel.bookmark.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.realkoreatravel.bookmark.domain.Bookmark;
import com.realkoreatravel.bookmark.dto.BookmarkCreateRequest;
import com.realkoreatravel.bookmark.dto.BookmarkResponse;
import com.realkoreatravel.bookmark.repository.BookmarkRepository;
import com.realkoreatravel.member.domain.Member;
import com.realkoreatravel.member.repository.MemberRepository;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.repository.PlaceRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

/** 즐겨찾기 등록의 대상 검증·중복 방지·저장 흐름을 검증하는 단위 테스트다. */
@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @Test
    @DisplayName("인증된 회원이 장소를 즐겨찾기에 등록한다")
    void createsBookmark() {
        Member member = Member.builder().email("user@example.com").providerId("provider-id").build();
        Place place = Place.builder().name("테스트 장소").address("테스트 주소").build();
        Bookmark bookmark = Bookmark.builder().member(member).place(place).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(placeRepository.findById(2L)).thenReturn(Optional.of(place));
        when(bookmarkRepository.existsByMemberIdAndPlaceId(1L, 2L)).thenReturn(false);
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);

        BookmarkResponse response = bookmarkService.create(1L, new BookmarkCreateRequest(2L));

        assertThat(response).isNotNull();
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("같은 회원이 같은 장소를 중복 등록하면 409 예외를 발생시킨다")
    void rejectsDuplicateBookmark() {
        Member member = Member.builder().email("user@example.com").providerId("provider-id").build();
        Place place = Place.builder().name("테스트 장소").address("테스트 주소").build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(placeRepository.findById(2L)).thenReturn(Optional.of(place));
        when(bookmarkRepository.existsByMemberIdAndPlaceId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> bookmarkService.create(1L, new BookmarkCreateRequest(2L)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("이미 즐겨찾기에 등록된 장소입니다.");

        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("존재하지 않는 장소는 즐겨찾기에 등록할 수 없다")
    void rejectsMissingPlace() {
        Member member = Member.builder().email("user@example.com").providerId("provider-id").build();
        when(memberRepository.findById(eq(1L))).thenReturn(Optional.of(member));
        when(placeRepository.findById(eq(2L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookmarkService.create(1L, new BookmarkCreateRequest(2L)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("장소를 찾을 수 없습니다.");

        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }
}
