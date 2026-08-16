package com.realkoreatravel.place.repository;

import com.realkoreatravel.place.domain.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCode(String code);
}
