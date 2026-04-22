package com.cpt202.booking.repository;

import com.cpt202.booking.model.Specialist;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {

    List<Specialist> findByNameContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(String nameKeyword, String categoryKeyword);

    boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

    Optional<Specialist> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select specialist from Specialist specialist where specialist.id = :id")
    Optional<Specialist> findByIdForUpdate(@Param("id") Long id);
}
