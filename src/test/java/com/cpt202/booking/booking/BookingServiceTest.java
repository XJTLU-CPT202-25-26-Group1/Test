package com.cpt202.booking.booking;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.Booking;
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

import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Test
    void createBookingMarksSlotBookedAndPending() {
        Specialist specialist = specialistRepository.findAll().get(0);
        AvailabilitySlot slot = availabilitySlotRepository
                .findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(specialist.getId(), java.time.LocalDate.now())
                .get(0);

        Booking booking = bookingService.createBooking(
                "Test Customer",
                "testcustomer@example.com",
                specialist.getId(),
                slot.getId(),
                "Need consultation",
                "Background information"
        );

        AvailabilitySlot updatedSlot = availabilitySlotRepository.findById(slot.getId()).orElseThrow();
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertTrue(updatedSlot.isBooked());
        assertNotNull(booking.getCalculatedFee());
    }

    @Test
    void rescheduleBookingReleasesOldSlotAndLocksNewSlot() {
        Specialist specialist = specialistRepository.findAll().stream()
                .max(Comparator.comparingLong(Specialist::getId))
                .orElseThrow();
        AvailabilitySlot originalSlot = availabilityService.createSlot(
                specialist.getId(),
                LocalDate.now().plusDays(5),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0)
        );
        AvailabilitySlot newSlot = availabilityService.createSlot(
                specialist.getId(),
                LocalDate.now().plusDays(6),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0)
        );

        Booking booking = bookingService.createBooking(
                "Test Customer",
                "reschedule@example.com",
                specialist.getId(),
                originalSlot.getId(),
                "Reschedule test",
                ""
        );
        bookingService.confirmBooking(booking.getId());

        Booking updated = bookingService.rescheduleBookingForCustomer(booking.getId(), newSlot.getId(), "reschedule@example.com");

        AvailabilitySlot refreshedOriginal = availabilitySlotRepository.findById(originalSlot.getId()).orElseThrow();
        AvailabilitySlot refreshedNew = availabilitySlotRepository.findById(newSlot.getId()).orElseThrow();

        assertEquals(BookingStatus.PENDING, updated.getStatus());
        assertEquals(newSlot.getId(), updated.getSlot().getId());
        assertFalse(refreshedOriginal.isBooked());
        assertTrue(refreshedNew.isBooked());
    }

    @Test
    void rejectBookingMarksRejectedAndReleasesSlot() {
        Specialist specialist = specialistRepository.findAll().get(0);
        AvailabilitySlot slot = availabilitySlotRepository
                .findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(specialist.getId(), LocalDate.now())
                .get(0);

        Booking booking = bookingService.createBooking(
                "Rejected Customer",
                "rejected@example.com",
                specialist.getId(),
                slot.getId(),
                "Rejected booking",
                ""
        );

        Booking rejected = bookingService.rejectBooking(booking.getId(), "Capacity full");
        AvailabilitySlot refreshedSlot = availabilitySlotRepository.findById(slot.getId()).orElseThrow();

        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
        assertEquals("Capacity full", rejected.getRejectionReason());
        assertFalse(refreshedSlot.isBooked());
    }

    @Test
    void rescheduleBookingRejectsSlotFromDifferentSpecialist() {
        List<Specialist> specialists = specialistRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Specialist::getId))
                .toList();
        Specialist originalSpecialist = specialists.get(0);
        Specialist differentSpecialist = specialists.get(1);

        AvailabilitySlot originalSlot = availabilityService.createSlot(
                originalSpecialist.getId(),
                LocalDate.now().plusDays(5),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0)
        );
        AvailabilitySlot differentSpecialistSlot = availabilityService.createSlot(
                differentSpecialist.getId(),
                LocalDate.now().plusDays(6),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0)
        );

        Booking booking = bookingService.createBooking(
                "Cross Specialist Customer",
                "cross-specialist@example.com",
                originalSpecialist.getId(),
                originalSlot.getId(),
                "Cross specialist reschedule",
                ""
        );
        bookingService.confirmBooking(booking.getId());

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> bookingService.rescheduleBookingForCustomer(
                        booking.getId(),
                        differentSpecialistSlot.getId(),
                        "cross-specialist@example.com"
                ));

        assertEquals("Booking can only be rescheduled to another slot for the same specialist.", error.getMessage());
    }

    @Test
    void futureBookingCannotBeCompletedEarly() {
        Specialist specialist = specialistRepository.findAll().get(0);
        AvailabilitySlot slot = availabilitySlotRepository
                .findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(specialist.getId(), LocalDate.now())
                .get(0);

        Booking booking = bookingService.createBooking(
                "Early Completion Customer",
                "early-completion@example.com",
                specialist.getId(),
                slot.getId(),
                "Early completion check",
                ""
        );
        bookingService.confirmBooking(booking.getId());

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> bookingService.completeBookingForSpecialist(booking.getId(), specialist.getId()));

        assertEquals("Booking can only be completed after the consultation has finished.", error.getMessage());
    }
}
