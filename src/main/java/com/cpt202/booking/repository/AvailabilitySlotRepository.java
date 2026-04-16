package com.cpt202.booking.repository;

import com.cpt202.booking.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findBySpecialistIdOrderBySlotDateAscStartTimeAsc(Long specialistId);

    List<AvailabilitySlot> findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(Long specialistId, LocalDate date);

    List<AvailabilitySlot> findBySpecialistIdAndBookedFalseAndSlotDateOrderByStartTimeAsc(Long specialistId, LocalDate date);

    Optional<AvailabilitySlot> findByIdAndSpecialistId(Long id, Long specialistId);
}
