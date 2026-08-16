package com.realkoreatravel.place.repository;

import com.realkoreatravel.place.domain.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCode(String code);
}
