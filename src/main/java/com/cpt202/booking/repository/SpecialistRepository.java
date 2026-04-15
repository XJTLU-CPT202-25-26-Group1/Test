package com.cpt202.booking.repository;

import com.cpt202.booking.model.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {

    List<Specialist> findByNameContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(String nameKeyword, String categoryKeyword);
} 
