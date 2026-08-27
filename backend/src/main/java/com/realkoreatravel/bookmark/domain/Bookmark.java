package com.realkoreatravel.bookmark.domain;

import com.realkoreatravel.member.domain.Member;
import com.realkoreatravel.place.domain.Place;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 회원이 저장한 장소를 나타내는 즐겨찾기 엔티티다. */
@Entity
@Table(name = "bookmark")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark {

    /** 즐겨찾기를 식별하는 데이터베이스 기본 키. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 즐겨찾기를 등록한 회원. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** 회원이 저장한 장소. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    /** 즐겨찾기가 등록된 시각. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** 즐겨찾기가 삭제된 시각이며, null이면 현재 활성 상태다. */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** 회원과 장소를 연결하고 즐겨찾기 생성 시각을 초기화한다. */
    @Builder
    public Bookmark(Member member, Place place) {
        this.member = member;
        this.place = place;
        this.createdAt = Instant.now();
    }

    /** 즐겨찾기를 실제로 삭제하지 않고 삭제 시각을 기록해 비활성화한다. */
    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    /** Soft Delete된 즐겨찾기를 다시 활성 상태로 복구한다. */
    public void restore() {
        this.deletedAt = null;
    }
}
