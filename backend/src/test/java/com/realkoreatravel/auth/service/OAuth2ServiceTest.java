package com.realkoreatravel.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.realkoreatravel.auth.dto.GoogleUserInfo;
import com.realkoreatravel.member.domain.Member;
import com.realkoreatravel.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuth2ServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private OAuth2Service oAuth2Service;

    private final GoogleUserInfo googleUserInfo = new GoogleUserInfo(
            "google-sub-123",
            "user@gmail.com",
            "홍길동",
            "https://example.com/picture.png"
    );

    @Test
    @DisplayName("기존 회원이면 조회만 하고 저장하지 않는다")
    void findOrCreateMember_existingMember_returnsExisting() {
        Member existing = Member.builder()
                .email("user@gmail.com")
                .nickname("홍길동")
                .profileImageUrl("https://example.com/picture.png")
                .provider("GOOGLE")
                .providerId("google-sub-123")
                .build();
        when(memberRepository.findByProviderAndProviderId("GOOGLE", "google-sub-123"))
                .thenReturn(Optional.of(existing));

        Member result = oAuth2Service.findOrCreateMember(googleUserInfo);

        assertThat(result).isSameAs(existing);
        verify(memberRepository).findByProviderAndProviderId("GOOGLE", "google-sub-123");
        verify(memberRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    @DisplayName("신규 회원이면 생성 후 저장한다")
    void findOrCreateMember_newMember_createsAndSaves() {
        when(memberRepository.findByProviderAndProviderId("GOOGLE", "google-sub-123"))
                .thenReturn(Optional.empty());
        Member saved = Member.builder()
                .email("user@gmail.com")
                .nickname("홍길동")
                .profileImageUrl("https://example.com/picture.png")
                .provider("GOOGLE")
                .providerId("google-sub-123")
                .build();
        when(memberRepository.save(any(Member.class))).thenReturn(saved);

        Member result = oAuth2Service.findOrCreateMember(googleUserInfo);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@gmail.com");
        assertThat(result.getProvider()).isEqualTo("GOOGLE");
        assertThat(result.getProviderId()).isEqualTo("google-sub-123");
        verify(memberRepository).save(any(Member.class));
    }
}
