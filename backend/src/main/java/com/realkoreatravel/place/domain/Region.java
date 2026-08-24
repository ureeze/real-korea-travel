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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {

    /** 지역을 식별하는 데이터베이스 기본 키 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 현재 지역의 상위 지역; 예를 들어 성수의 상위 지역인 서울 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Region parent;

    /** 사용자에게 표시할 지역명 */
    @Column(nullable = false, length = 50)
    private String name;

    /** API와 DB 조회에 사용하는 지역 고유 코드 */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** 지역 목록에서 표시할 순서 */
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Builder
    public Region(Region parent, String name, String code, int displayOrder) {
        this.parent = parent;
        this.name = name;
        this.code = code;
        this.displayOrder = displayOrder;
    }

}
