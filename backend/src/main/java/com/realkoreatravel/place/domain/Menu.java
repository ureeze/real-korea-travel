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

    /** 메뉴를 식별하는 데이터베이스 기본 키. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 메뉴가 속한 장소. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    /** 메뉴의 한국어 이름. */
    @Column(nullable = false, length = 100)
    private String name;

    /** 메뉴의 영어 이름. */
    @Column(name = "name_en", length = 100)
    private String nameEn;

    /** 메뉴 가격. */
    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal price;

    /** 대표 메뉴 여부. */
    @Column(name = "is_signature", nullable = false)
    private boolean signature;

    /** 메뉴에 대한 설명. */
    @Column(length = 500)
    private String description;

    /** 메뉴 이미지 URL. */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** 메뉴 노출 순서. */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    /** 메뉴가 생성된 시각. */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** 메뉴가 마지막으로 수정된 시각. */
    @Column(name = "updated_at", nullable = false)
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
