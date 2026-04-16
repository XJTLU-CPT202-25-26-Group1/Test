package com.cpt202.booking.service;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.Feedback;
import com.cpt202.booking.repository.BookingRepository;
import com.cpt202.booking.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookingRepository bookingRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, BookingRepository bookingRepository) {
        this.feedbackRepository = feedbackRepository;
        this.bookingRepository = bookingRepository;
    }

    public Feedback submitFeedback(Long bookingId, String customerEmail, Integer rating, String comment) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (!booking.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
            throw new IllegalArgumentException("You can only submit feedback for your own bookings.");
        }
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Feedback is available only after the consultation is completed.");
        }
        if (feedbackRepository.findByBookingId(bookingId).isPresent()) {
            throw new IllegalStateException("Feedback has already been submitted for this booking.");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Feedback feedback = new Feedback();
        feedback.setBooking(booking);
        feedback.setSpecialist(booking.getSpecialist());
        feedback.setCustomerEmail(booking.getCustomerEmail());
        feedback.setCustomerName(booking.getCustomerName());
        feedback.setRating(rating);
        feedback.setComment(comment);
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackForSpecialist(Long specialistId) {
        return feedbackRepository.findBySpecialistIdOrderByCreatedAtDesc(specialistId);
    }

    public Optional<Feedback> getFeedbackForBooking(Long bookingId) {
        return feedbackRepository.findByBookingId(bookingId);
    }
}
