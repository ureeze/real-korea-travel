package com.realkoreatravel.bookmark.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.realkoreatravel.bookmark.domain.Bookmark;
import com.realkoreatravel.member.domain.Member;
import com.realkoreatravel.member.repository.MemberRepository;
import com.realkoreatravel.place.domain.Category;
import com.realkoreatravel.place.domain.Place;
import com.realkoreatravel.place.domain.Region;
import com.realkoreatravel.place.repository.CategoryRepository;
import com.realkoreatravel.place.repository.PlaceRepository;
import com.realkoreatravel.place.repository.RegionRepository;
import com.realkoreatravel.support.PostgresTestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

/** 즐겨찾기 JPA 매핑과 Soft Delete 후 재등록 제약을 PostgreSQL에서 검증한다. */
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresTestcontainersConfiguration.class)
class BookmarkRepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("회원과 장소 기준으로 즐겨찾기를 조회한다")
    void findsBookmarkByMemberAndPlace() {
        Member member = memberRepository.save(Member.builder()
                .email("bookmark@example.com")
                .provider("GOOGLE")
                .providerId("bookmark-provider")
                .build());
        Place place = savePlace("bookmark-region", "bookmark-category");
        Bookmark expected = bookmarkRepository.saveAndFlush(Bookmark.builder()
                .member(member)
                .place(place)
                .build());

        Bookmark actual = bookmarkRepository.findByMemberIdAndPlaceIdAndDeletedAtIsNull(member.getId(), place.getId())
                .orElseThrow();

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getMember().getId()).isEqualTo(member.getId());
        assertThat(actual.getPlace().getId()).isEqualTo(place.getId());
    }

    @Test
    @DisplayName("같은 회원이 같은 장소를 중복 등록할 수 없다")
    void duplicateMemberPlaceViolatesUniqueConstraint() {
        Member member = memberRepository.save(Member.builder()
                .email("duplicate@example.com")
                .provider("GOOGLE")
                .providerId("duplicate-provider")
                .build());
        Place place = savePlace("duplicate-bookmark-region", "duplicate-bookmark-category");
        bookmarkRepository.saveAndFlush(Bookmark.builder().member(member).place(place).build());

        assertThatThrownBy(() -> bookmarkRepository.saveAndFlush(
                Bookmark.builder().member(member).place(place).build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Soft Delete된 즐겨찾기는 같은 회원이 다시 등록할 수 있다")
    void deletedBookmarkCanBeRegisteredAgain() {
        Member member = memberRepository.save(Member.builder()
                .email("re-register@example.com")
                .provider("GOOGLE")
                .providerId("re-register-provider")
                .build());
        Place place = savePlace("re-register-region", "re-register-category");

        Bookmark bookmark = bookmarkRepository.saveAndFlush(Bookmark.builder()
                .member(member)
                .place(place)
                .build());
        bookmark.softDelete();
        bookmarkRepository.saveAndFlush(bookmark);

        Bookmark reRegisteredBookmark = bookmarkRepository.saveAndFlush(Bookmark.builder()
                .member(member)
                .place(place)
                .build());

        assertThat(bookmark.getDeletedAt()).isNotNull();
        assertThat(reRegisteredBookmark.getId()).isNotEqualTo(bookmark.getId());
        assertThat(bookmarkRepository.findByMemberIdAndPlaceIdAndDeletedAtIsNull(member.getId(), place.getId()))
                .contains(reRegisteredBookmark);
    }

    /** Repository 테스트에서 사용할 지역·카테고리·장소 데이터를 생성한다. */
    private Place savePlace(String regionCode, String categoryCode) {
        Region region = regionRepository.save(Region.builder()
                .name(regionCode)
                .code(regionCode)
                .displayOrder(99)
                .build());
        Category category = categoryRepository.save(Category.builder()
                .name(categoryCode)
                .code(categoryCode)
                .displayOrder(99)
                .build());
        return placeRepository.saveAndFlush(Place.builder()
                .region(region)
                .category(category)
                .name("테스트 장소")
                .address("테스트 주소")
                .build());
    }
}
