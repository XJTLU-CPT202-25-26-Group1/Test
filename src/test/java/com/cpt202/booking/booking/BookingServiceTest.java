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
}
