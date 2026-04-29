package com.cpt202.booking.repository;

import com.cpt202.booking.model.AvailabilitySlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findBySpecialistIdOrderBySlotDateAscStartTimeAsc(Long specialistId);

    List<AvailabilitySlot> findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(Long specialistId, LocalDate date);

    List<AvailabilitySlot> findBySpecialistIdAndBookedFalseAndSlotDateOrderByStartTimeAsc(Long specialistId, LocalDate date);

    boolean existsBySpecialistIdAndBookedTrue(Long specialistId);

    long deleteBySpecialistId(Long specialistId);

    Optional<AvailabilitySlot> findByIdAndSpecialistId(Long id, Long specialistId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select slot from AvailabilitySlot slot where slot.id = :id")
    Optional<AvailabilitySlot> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select slot from AvailabilitySlot slot where slot.id = :id and slot.specialist.id = :specialistId")
    Optional<AvailabilitySlot> findByIdAndSpecialistIdForUpdate(@Param("id") Long id, @Param("specialistId") Long specialistId);
}
