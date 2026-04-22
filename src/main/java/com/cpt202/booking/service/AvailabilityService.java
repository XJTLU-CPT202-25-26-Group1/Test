package com.cpt202.booking.service;

import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AvailabilityService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final SpecialistRepository specialistRepository;

    public AvailabilityService(AvailabilitySlotRepository availabilitySlotRepository, SpecialistRepository specialistRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.specialistRepository = specialistRepository;
    }

    public List<AvailabilitySlot> getSlotsForSpecialist(Long specialistId) {
        return availabilitySlotRepository.findBySpecialistIdOrderBySlotDateAscStartTimeAsc(specialistId);
    }

    public List<AvailabilitySlot> getAvailableSlots(Long specialistId) {
        return availabilitySlotRepository.findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(
                specialistId,
                LocalDate.now()
        ).stream()
                .filter(this::isSlotInFuture)
                .toList();
    }

    public List<AvailabilitySlot> getAvailableSlots(Long specialistId, LocalDate slotDate) {
        if (slotDate == null) {
            return getAvailableSlots(specialistId);
        }
        return availabilitySlotRepository.findBySpecialistIdAndBookedFalseAndSlotDateOrderByStartTimeAsc(specialistId, slotDate)
                .stream()
                .filter(this::isSlotInFuture)
                .toList();
    }

    @Transactional
    public AvailabilitySlot createSlot(Long specialistId, LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        if (slotDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Slot date cannot be in the past.");
        }
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("End time must be later than start time.");
        }
        validateSlotStartsInFuture(slotDate, startTime);

        Specialist specialist = specialistRepository.findByIdForUpdate(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found."));

        List<AvailabilitySlot> existingSlots = availabilitySlotRepository.findBySpecialistIdOrderBySlotDateAscStartTimeAsc(specialistId);
        validateOverlap(existingSlots, slotDate, startTime, endTime, null);

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setSpecialist(specialist);
        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setBooked(false);
        return availabilitySlotRepository.save(slot);
    }

    @Transactional
    public AvailabilitySlot updateSlot(Long specialistId, Long slotId, LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        if (slotDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Slot date cannot be in the past.");
        }
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("End time must be later than start time.");
        }
        validateSlotStartsInFuture(slotDate, startTime);

        specialistRepository.findByIdForUpdate(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found."));

        AvailabilitySlot slot = availabilitySlotRepository.findByIdAndSpecialistIdForUpdate(slotId, specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found."));

        if (slot.isBooked()) {
            throw new IllegalStateException("Booked slots cannot be edited.");
        }

        List<AvailabilitySlot> existingSlots = availabilitySlotRepository.findBySpecialistIdOrderBySlotDateAscStartTimeAsc(specialistId);
        validateOverlap(existingSlots, slotDate, startTime, endTime, slotId);

        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        return availabilitySlotRepository.save(slot);
    }

    @Transactional
    public void deleteSlot(Long specialistId, Long slotId) {
        specialistRepository.findByIdForUpdate(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found."));

        AvailabilitySlot slot = availabilitySlotRepository.findByIdAndSpecialistIdForUpdate(slotId, specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found."));

        if (slot.isBooked()) {
            throw new IllegalStateException("Booked slots cannot be deleted.");
        }

        availabilitySlotRepository.delete(slot);
    }

    private void validateOverlap(List<AvailabilitySlot> existingSlots,
                                 LocalDate slotDate,
                                 LocalTime startTime,
                                 LocalTime endTime,
                                 Long ignoreSlotId) {
        for (AvailabilitySlot existing : existingSlots) {
            if (!existing.getSlotDate().equals(slotDate)) {
                continue;
            }
            if (ignoreSlotId != null && ignoreSlotId.equals(existing.getId())) {
                continue;
            }
            boolean overlaps = startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime());
            if (overlaps) {
                throw new IllegalArgumentException("Slot overlaps with an existing slot.");
            }
        }
    }

    private void validateSlotStartsInFuture(LocalDate slotDate, LocalTime startTime) {
        if (!LocalDateTime.of(slotDate, startTime).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Slot start time must be in the future.");
        }
    }

    private boolean isSlotInFuture(AvailabilitySlot slot) {
        return slot.getSlotDate() != null
                && slot.getStartTime() != null
                && LocalDateTime.of(slot.getSlotDate(), slot.getStartTime()).isAfter(LocalDateTime.now());
    }
}
