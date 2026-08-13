package com.realkoreatravel.auth.service;

import com.realkoreatravel.auth.dto.GoogleUserInfo;
import com.realkoreatravel.member.domain.Member;
import com.realkoreatravel.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2Service {

    private static final String GOOGLE = "GOOGLE";

    private final MemberRepository memberRepository;

    public OAuth2Service(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /** Google의 고유 사용자 ID를 기준으로 기존 회원을 재사용하거나 신규 회원을 저장한다. */
    @Transactional
    public Member findOrCreateMember(GoogleUserInfo userInfo) {
        // 이메일이 변경되더라도 같은 Google 계정으로 인식할 수 있도록 provider와 sub를 함께 사용한다.
        return memberRepository.findByProviderAndProviderId(GOOGLE, userInfo.sub())
                .orElseGet(() -> createMember(userInfo));
    }

    /** Google에서 받은 기본 프로필 정보로 최초 로그인 회원을 생성한다. */
    private Member createMember(GoogleUserInfo userInfo) {
        Member member = Member.builder()
                .email(userInfo.email())
                .nickname(userInfo.name())
                .profileImageUrl(userInfo.picture())
                .provider(GOOGLE)
                .providerId(userInfo.sub())
                .build();
        return memberRepository.save(member);
    }
}
