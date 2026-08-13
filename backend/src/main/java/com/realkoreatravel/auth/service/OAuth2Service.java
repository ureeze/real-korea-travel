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

    @Transactional
    public Member findOrCreateMember(GoogleUserInfo userInfo) {
        return memberRepository.findByProviderAndProviderId(GOOGLE, userInfo.sub())
                .orElseGet(() -> createMember(userInfo));
    }

    private Member createMember(GoogleUserInfo userInfo) {
        Member member = new Member(
                userInfo.email(),
                userInfo.name(),
                userInfo.picture(),
                GOOGLE,
                userInfo.sub()
        );
        return memberRepository.save(member);
    }
}
