package com.cpt202.booking.repository;

import com.cpt202.booking.model.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
}