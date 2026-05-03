package com.cpt202.booking.feedback;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.BookingRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FeedbackServiceTest {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Test
    void submitFeedbackRejectsOverlongComment() {
        Specialist specialist = specialistRepository.findAll().get(0);

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setSpecialist(specialist);
        slot.setSlotDate(LocalDate.now().minusDays(1));
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(10, 0));
        slot.setBooked(true);
        slot = availabilitySlotRepository.save(slot);

        Booking booking = new Booking();
        booking.setCustomerName("Feedback Customer");
        booking.setCustomerEmail("feedback-length@example.com");
        booking.setSpecialist(specialist);
        booking.setSlot(slot);
        booking.setTopic("Completed consultation");
        booking.setStatus(BookingStatus.COMPLETED);
        booking = bookingRepository.save(booking);
        Long bookingId = booking.getId();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> feedbackService.submitFeedback(
                        bookingId,
                        "feedback-length@example.com",
                        5,
                        "a".repeat(256)
                ));

        assertEquals("Feedback comment is too long. Please shorten it.", error.getMessage());
    }
}
