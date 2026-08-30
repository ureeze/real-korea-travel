package com.realkoreatravel.place.domain;

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
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 장소의 요일별 운영시간을 저장한다. */
@Entity
@Table(name = "opening_hour")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpeningHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "day_of_week", nullable = false)
    private short dayOfWeek;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "is_closed", nullable = false)
    private boolean closed;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** 장소의 특정 요일 운영시간을 생성한다. */
    @Builder
    public OpeningHour(Place place, short dayOfWeek, LocalTime openTime, LocalTime closeTime, boolean closed) {
        this.place = place;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.closed = closed;
        if (place != null) {
            place.addOpeningHour(this);
        }
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
