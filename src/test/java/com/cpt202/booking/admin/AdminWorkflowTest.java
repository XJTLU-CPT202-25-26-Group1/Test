package com.cpt202.booking.admin;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.enums.GenderType;
import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.model.User;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.repository.UserRepository;
import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.SpecialistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    void adminCanApprovePendingSpecialistRegistration() {
        Specialist specialist = specialistRepository.save(new Specialist(
                "Pending Specialist",
                "Associate",
                180.0,
                "Waiting for approval.",
                SpecialistStatus.PENDING_APPROVAL,
                specialistRepository.findAll().get(0).getCategory()
        ));
        User user = new User("pending-admin-review", "encoded", "Pending Specialist",
                "pending-admin-review@example.com", "13800005555", GenderType.FEMALE, RoleType.SPECIALIST);
        user.setEmailVerified(true);
        user.setSpecialistId(specialist.getId());
        userRepository.save(user);

        specialistService.approveSpecialist(specialist.getId());

        assertEquals(SpecialistStatus.ACTIVE, specialistRepository.findById(specialist.getId()).orElseThrow().getStatus());
    }

    @Test
    void adminCanRejectPendingSpecialistRegistration() {
        Specialist specialist = specialistRepository.save(new Specialist(
                "Rejected Specialist",
                "Associate",
                180.0,
                "Waiting for approval.",
                SpecialistStatus.PENDING_APPROVAL,
                specialistRepository.findAll().get(0).getCategory()
        ));
        User user = new User("rejected-admin-review", "encoded", "Rejected Specialist",
                "rejected-admin-review@example.com", "13800006666", GenderType.MALE, RoleType.SPECIALIST);
        user.setEmailVerified(true);
        user.setSpecialistId(specialist.getId());
        userRepository.save(user);

        specialistService.rejectSpecialist(specialist.getId());

        assertEquals(SpecialistStatus.REJECTED, specialistRepository.findById(specialist.getId()).orElseThrow().getStatus());
    }

    @Test
    void adminCannotRejectActiveSpecialistUsingPendingApprovalAction() {
        Specialist specialist = specialistRepository.findAll().get(0);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> specialistService.rejectSpecialist(specialist.getId()));

        assertEquals("Only pending specialist registrations can be rejected.", error.getMessage());
    }
}
