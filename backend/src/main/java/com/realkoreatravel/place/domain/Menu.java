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
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 메뉴를 식별하는 데이터베이스 기본 키. */
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    /** 메뉴가 속한 장소. */
    private Place place;

    @Column(nullable = false, length = 100)
    /** 메뉴의 한국어 이름. */
    private String name;

    @Column(name = "name_en", length = 100)
    /** 메뉴의 영어 이름. */
    private String nameEn;

    @Column(nullable = false, precision = 10, scale = 0)
    /** 메뉴 가격. */
    private BigDecimal price;

    @Column(name = "is_signature", nullable = false)
    /** 대표 메뉴 여부. */
    private boolean signature;

    @Column(length = 500)
    /** 메뉴에 대한 설명. */
    private String description;

    @Column(name = "image_url", length = 500)
    /** 메뉴 이미지 URL. */
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    /** 메뉴 노출 순서. */
    private int sortOrder;

    @Column(name = "created_at", nullable = false)
    /** 메뉴가 생성된 시각. */
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    /** 메뉴가 마지막으로 수정된 시각. */
    private Instant updatedAt;

    /** 장소의 메뉴 정보를 생성하고 생성·수정 시각을 초기화한다. */
    @Builder
    public Menu(
            Place place,
            String name,
            String nameEn,
            BigDecimal price,
            boolean signature,
            String description,
            String imageUrl,
            int sortOrder
    ) {
        this.place = place;
        this.name = name;
        this.nameEn = nameEn;
        this.price = price;
        this.signature = signature;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        if (place != null) {
            place.addMenu(this);
        }
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
