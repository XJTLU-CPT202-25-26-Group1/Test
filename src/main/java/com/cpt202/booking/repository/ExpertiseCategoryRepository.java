package com.cpt202.booking.repository;

import com.cpt202.booking.model.ExpertiseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpertiseCategoryRepository extends JpaRepository<ExpertiseCategory, Long> {

    Optional<ExpertiseCategory> findByNameIgnoreCase(String name);
}
