package com.cpt202.booking.service;

import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        );
    }

    public AvailabilitySlot createSlot(Long specialistId, LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("End time must be later than start time.");
        }

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found."));

        List<AvailabilitySlot> existingSlots = availabilitySlotRepository.findBySpecialistIdOrderBySlotDateAscStartTimeAsc(specialistId);
        for (AvailabilitySlot existing : existingSlots) {
            if (!existing.getSlotDate().equals(slotDate)) {
                continue;
            }
            boolean overlaps = startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime());
            if (overlaps) {
                throw new IllegalArgumentException("Slot overlaps with an existing slot.");
            }
        }

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setSpecialist(specialist);
        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setBooked(false);
        return availabilitySlotRepository.save(slot);
    }
}
