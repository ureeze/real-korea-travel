package com.realkoreatravel.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_feature")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceFeature {

    /** 편의정보를 식별하는 데이터베이스 기본 키. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 편의정보가 속한 장소. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false, unique = true)
    private Place place;

    /** 영어 메뉴 제공 여부. */
    @Column(name = "english_menu", nullable = false)
    private boolean englishMenu;

    /** 카드 결제 가능 여부. */
    @Column(name = "card_available", nullable = false)
    private boolean cardAvailable;

    /** 혼자 방문하기 좋은 장소인지 여부. */
    @Column(name = "solo_friendly", nullable = false)
    private boolean soloFriendly;

    /** 방문 전 예약 필요 여부. */
    @Column(name = "reservation_required", nullable = false)
    private boolean reservationRequired;

    /** 주차 가능 여부. */
    @Column(name = "parking_available", nullable = false)
    private boolean parkingAvailable;

    /** 평균 대기시간(분). */
    @Column(name = "avg_wait_time_min", nullable = false)
    private Integer avgWaitTimeMin;

    /** 편의정보가 생성된 시각. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** 편의정보가 마지막으로 수정된 시각. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** 장소의 외국인 편의정보를 생성하고 생성·수정 시각을 초기화한다. */
    @Builder
    public PlaceFeature(
            Place place,
            boolean englishMenu,
            boolean cardAvailable,
            boolean soloFriendly,
            boolean reservationRequired,
            boolean parkingAvailable,
            Integer avgWaitTimeMin
    ) {
        this.place = place;
        this.englishMenu = englishMenu;
        this.cardAvailable = cardAvailable;
        this.soloFriendly = soloFriendly;
        this.reservationRequired = reservationRequired;
        this.parkingAvailable = parkingAvailable;
        this.avgWaitTimeMin = avgWaitTimeMin;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
