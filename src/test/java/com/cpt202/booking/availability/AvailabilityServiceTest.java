package com.cpt202.booking.availability;

import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.service.AvailabilityService;
import com.cpt202.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AvailabilityServiceTest {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Autowired
    private BookingService bookingService;

    @Test
    void overlappingSlotIsRejected() {
        Specialist specialist = specialistRepository.findAll().get(0);
        LocalDate slotDate = availabilityService.getSlotsForSpecialist(specialist.getId()).get(0).getSlotDate();

        assertThrows(IllegalArgumentException.class, () -> availabilityService.createSlot(
                specialist.getId(),
                slotDate,
                LocalTime.of(10, 30),
                LocalTime.of(11, 30)
        ));
    }

    @Test
    void bookedSlotCannotBeEditedOrDeleted() {
        Specialist specialist = specialistRepository.findAll().get(0);
        var slot = availabilitySlotRepository
                .findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(specialist.getId(), LocalDate.now())
                .get(0);

        bookingService.createBooking(
                "Booked Customer",
                "booked@example.com",
                specialist.getId(),
                slot.getId(),
                "Booked slot protection",
                ""
        );

        assertThrows(IllegalStateException.class, () -> availabilityService.updateSlot(
                specialist.getId(),
                slot.getId(),
                slot.getSlotDate(),
                slot.getStartTime().plusHours(1),
                slot.getEndTime().plusHours(1)
        ));

        assertThrows(IllegalStateException.class, () -> availabilityService.deleteSlot(
                specialist.getId(),
                slot.getId()
        ));
    }

    @Test
    void sameDayPastStartTimeIsRejectedAndHiddenFromAvailableSlots() {
        Specialist specialist = specialistRepository.findAll().get(0);
        LocalTime pastStart = LocalTime.now().minusHours(2).withSecond(0).withNano(0);
        LocalTime pastEnd = pastStart.plusHours(1);

        assertThrows(IllegalArgumentException.class, () -> availabilityService.createSlot(
                specialist.getId(),
                LocalDate.now(),
                pastStart,
                pastEnd
        ));

        var hiddenPastSlot = new com.cpt202.booking.model.AvailabilitySlot();
        hiddenPastSlot.setSpecialist(specialist);
        hiddenPastSlot.setSlotDate(LocalDate.now());
        hiddenPastSlot.setStartTime(pastStart);
        hiddenPastSlot.setEndTime(pastEnd);
        hiddenPastSlot.setBooked(false);
        availabilitySlotRepository.save(hiddenPastSlot);

        List<com.cpt202.booking.model.AvailabilitySlot> availableSlots = availabilityService.getAvailableSlots(specialist.getId());
        assertFalse(availableSlots.stream().anyMatch(slot -> slot.getId().equals(hiddenPastSlot.getId())));
    }

    @Test
    void updatingSlotToPastStartTimeTodayIsRejected() {
        Specialist specialist = specialistRepository.findAll().get(0);
        var slot = availabilitySlotRepository
                .findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(specialist.getId(), LocalDate.now())
                .stream()
                .filter(existing -> existing.getSlotDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElseThrow();

        LocalTime pastStart = LocalTime.now().minusHours(2).withSecond(0).withNano(0);
        LocalTime pastEnd = pastStart.plusHours(1);

        assertThrows(IllegalArgumentException.class, () -> availabilityService.updateSlot(
                specialist.getId(),
                slot.getId(),
                LocalDate.now(),
                pastStart,
                pastEnd
        ));
    }
}
