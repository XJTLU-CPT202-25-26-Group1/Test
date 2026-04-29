package com.cpt202.booking.repository;

import com.cpt202.booking.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findBySpecialistIdOrderByCreatedAtDesc(Long specialistId);

    boolean existsBySpecialistId(Long specialistId);

    Optional<Feedback> findByBookingId(Long bookingId);
}
