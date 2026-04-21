package com.cpt202.booking.admin;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminWorkflowTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Test
    void adminConfirmationWritesAuditTrail() {
        Specialist specialist = specialistRepository.findAll().get(0);
        AvailabilitySlot slot = availabilitySlotRepository
                .findBySpecialistIdAndBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(specialist.getId(), LocalDate.now())
                .get(0);

        Booking booking = bookingService.createBooking(
                "Workflow Customer",
                "workflow@example.com",
                specialist.getId(),
                slot.getId(),
                "Approval flow",
                ""
        );

        bookingService.confirmBooking(booking.getId());

        assertEquals(BookingStatus.CONFIRMED, bookingService.getBookingDetail(booking.getId()).getStatus());
        assertEquals("CONFIRMED", bookingService.getAuditLogsForBooking(booking.getId()).get(0).getNewStatus());
    }
}
