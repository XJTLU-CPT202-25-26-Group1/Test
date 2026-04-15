package com.cpt202.booking.service;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.BookingRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SpecialistRepository specialistRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public BookingService(BookingRepository bookingRepository,
                          SpecialistRepository specialistRepository,
                          AvailabilitySlotRepository availabilitySlotRepository) {
        this.bookingRepository = bookingRepository;
        this.specialistRepository = specialistRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatusOrderByCreatedAtAsc(BookingStatus.PENDING);
    }

    public List<Booking> getCustomerBookings(String customerEmail) {
        return bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);
    }

    public List<Booking> getSpecialistBookings(Long specialistId) {
        return bookingRepository.findBySpecialistIdOrderByCreatedAtDesc(specialistId);
    }

    public Booking createBooking(String customerName, String customerEmail, Long specialistId, Long slotId, String topic, String notes) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found."));

        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found."));

        if (!slot.getSpecialist().getId().equals(specialistId)) {
            throw new IllegalArgumentException("Slot does not belong to specialist.");
        }

        if (slot.isBooked()) {
            throw new IllegalArgumentException("Selected slot is no longer available.");
        }

        Booking booking = new Booking();
        booking.setCustomerName(customerName);
        booking.setCustomerEmail(customerEmail);
        booking.setSpecialist(specialist);
        booking.setSlot(slot);
        booking.setTopic(topic);
        booking.setNotes(notes);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCalculatedFee(calculateFee(specialist, slot));

        slot.setBooked(true);
        availabilitySlotRepository.save(slot);
        return bookingRepository.save(booking);
    }

    public Booking confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public Booking rejectBooking(Long id, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be rejected.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setRejectionReason(reason);
        releaseSlot(booking);
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending or confirmed bookings can be cancelled.");
        }

        if (isWithin24Hours(booking)) {
            throw new IllegalStateException("Cancellation is not allowed within 24 hours.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        releaseSlot(booking);
        return bookingRepository.save(booking);
    }

    public Booking rescheduleBooking(Long bookingId, Long newSlotId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be rescheduled.");
        }

        if (isWithin24Hours(booking)) {
            throw new IllegalStateException("Rescheduling is not allowed within 24 hours.");
        }

        AvailabilitySlot newSlot = availabilitySlotRepository.findById(newSlotId)
                .orElseThrow(() -> new IllegalArgumentException("New slot not found."));

        if (newSlot.isBooked()) {
            throw new IllegalStateException("New slot is already booked.");
        }

        releaseSlot(booking);
        newSlot.setBooked(true);
        availabilitySlotRepository.save(newSlot);

        booking.setSlot(newSlot);
        booking.setSpecialist(newSlot.getSpecialist());
        booking.setCalculatedFee(calculateFee(newSlot.getSpecialist(), newSlot));
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public Booking completeBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be completed.");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        return bookingRepository.save(booking);
    }

    private boolean isWithin24Hours(Booking booking) {
        LocalDateTime bookingTime = LocalDateTime.of(
                booking.getSlot().getSlotDate(),
                booking.getSlot().getStartTime()
        );
        return Duration.between(LocalDateTime.now(), bookingTime).toHours() < 24;
    }

    private void releaseSlot(Booking booking) {
        AvailabilitySlot slot = booking.getSlot();
        slot.setBooked(false);
        availabilitySlotRepository.save(slot);
    }

    private double calculateFee(Specialist specialist, AvailabilitySlot slot) {
        long minutes = Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        double hours = minutes / 60.0;
        return Math.round(specialist.getFeeRate() * hours * 100.0) / 100.0;
    }
}
