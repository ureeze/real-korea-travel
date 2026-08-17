package com.realkoreatravel.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    /** 회원을 식별하는 데이터베이스 기본 키. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 회원의 이메일 주소이며 중복 가입을 방지하기 위해 고유하게 관리한다. */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /** 화면에 표시할 회원 닉네임. */
    @Column(length = 50)
    private String nickname;

    /** 회원 프로필 이미지의 외부 URL. */
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    /** 회원이 사용하는 언어 코드이며 기본값은 영어(en)다. */
    @Column(length = 10, nullable = false)
    private String language = "en";

    /** 회원 인증에 사용한 외부 인증 제공자이며 기본값은 Google이다. */
    @Column(length = 20, nullable = false)
    private String provider = "GOOGLE";

    /** 외부 인증 제공자가 발급한 회원 식별자. */
    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    /** 회원이 최초 생성된 시각. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** 회원 정보가 마지막으로 변경된 시각. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** OAuth 회원 정보를 생성하고 생성·수정 시각을 현재 시각으로 초기화한다. */
    @Builder
    public Member(String email, String nickname, String profileImageUrl, String provider, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
        this.providerId = providerId;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
