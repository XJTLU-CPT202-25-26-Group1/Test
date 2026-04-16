package com.cpt202.booking.service;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.BookingAuditLog;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.BookingAuditLogRepository;
import com.cpt202.booking.repository.BookingRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SpecialistRepository specialistRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final BookingAuditLogRepository bookingAuditLogRepository;

    public BookingService(BookingRepository bookingRepository,
                          SpecialistRepository specialistRepository,
                          AvailabilitySlotRepository availabilitySlotRepository,
                          BookingAuditLogRepository bookingAuditLogRepository) {
        this.bookingRepository = bookingRepository;
        this.specialistRepository = specialistRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.bookingAuditLogRepository = bookingAuditLogRepository;
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

    public List<Booking> searchCustomerBookings(String customerEmail, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getCustomerBookings(customerEmail);
        }
        String lowerKeyword = keyword.toLowerCase();
        return bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail)
                .stream()
                .filter(booking -> booking.getTopic().toLowerCase().contains(lowerKeyword)
                        || booking.getSpecialist().getName().toLowerCase().contains(lowerKeyword)
                        || booking.getStatus().name().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<Booking> getSpecialistBookings(Long specialistId) {
        return bookingRepository.findBySpecialistIdOrderByCreatedAtDesc(specialistId);
    }

    public Booking getCustomerBookingDetail(Long bookingId, String customerEmail) {
        return bookingRepository.findByIdAndCustomerEmail(bookingId, customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
    }

    public List<Booking> getUpcomingBookings(String customerEmail) {
        return bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail)
                .stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .filter(booking -> LocalDateTime.of(booking.getSlot().getSlotDate(), booking.getSlot().getStartTime()).isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(booking -> LocalDateTime.of(booking.getSlot().getSlotDate(), booking.getSlot().getStartTime())))
                .collect(Collectors.toList());
    }

    public boolean canCustomerSelfManage(Booking booking) {
        return !isWithin24Hours(booking);
    }

    public List<BookingAuditLog> getAuditLogs() {
        return bookingAuditLogRepository.findAllByOrderByOperatedAtDesc();
    }

    public List<BookingAuditLog> getCustomerNotifications(String customerEmail) {
        Set<Long> bookingIds = bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail)
                .stream()
                .map(Booking::getId)
                .collect(Collectors.toCollection(HashSet::new));
        return bookingAuditLogRepository.findAllByOrderByOperatedAtDesc()
                .stream()
                .filter(log -> bookingIds.contains(log.getBookingId()))
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<BookingAuditLog> getSpecialistNotifications(Long specialistId) {
        Set<Long> bookingIds = bookingRepository.findBySpecialistIdOrderByCreatedAtDesc(specialistId)
                .stream()
                .map(Booking::getId)
                .collect(Collectors.toCollection(HashSet::new));
        return bookingAuditLogRepository.findAllByOrderByOperatedAtDesc()
                .stream()
                .filter(log -> bookingIds.contains(log.getBookingId()))
                .limit(5)
                .collect(Collectors.toList());
    }

    public Booking createBooking(String customerName, String customerEmail, Long specialistId, Long slotId, String topic, String notes) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Consultation topic is required.");
        }

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

        LocalDateTime slotDateTime = LocalDateTime.of(slot.getSlotDate(), slot.getStartTime());
        if (slotDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Selected slot must be in the future.");
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
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), null, BookingStatus.PENDING, customerEmail, "Booking created");
        return saved;
    }

    public Booking confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed.");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.CONFIRMED, "admin", "Booking confirmed");
        return saved;
    }

    public Booking rejectBooking(Long id, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be rejected.");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setRejectionReason(reason);
        releaseSlot(booking);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.CANCELLED, "admin", "Booking rejected: " + reason);
        return saved;
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

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        releaseSlot(booking);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.CANCELLED, booking.getCustomerEmail(), "Booking cancelled by customer");
        return saved;
    }

    public Booking cancelBookingForCustomer(Long id, String customerEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
        if (!booking.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
            throw new IllegalArgumentException("You can only cancel your own booking.");
        }
        return cancelBooking(id);
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

        BookingStatus oldStatus = booking.getStatus();
        booking.setSlot(newSlot);
        booking.setSpecialist(newSlot.getSpecialist());
        booking.setCalculatedFee(calculateFee(newSlot.getSpecialist(), newSlot));
        booking.setStatus(BookingStatus.PENDING);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.PENDING, booking.getCustomerEmail(), "Booking rescheduled and returned to pending");
        return saved;
    }

    public Booking rescheduleBookingForCustomer(Long bookingId, Long newSlotId, String customerEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
        if (!booking.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
            throw new IllegalArgumentException("You can only reschedule your own booking.");
        }
        return rescheduleBooking(bookingId, newSlotId);
    }

    public Booking completeBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be completed.");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.COMPLETED);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.COMPLETED, "specialist", "Booking marked completed");
        return saved;
    }

    public Booking completeBookingForSpecialist(Long id, Long specialistId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
        if (!booking.getSpecialist().getId().equals(specialistId)) {
            throw new IllegalArgumentException("You can only complete your own consultation bookings.");
        }
        return completeBooking(id);
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

    private void createAuditLog(Long bookingId,
                                BookingStatus oldStatus,
                                BookingStatus newStatus,
                                String operatorUsername,
                                String remark) {
        BookingAuditLog log = new BookingAuditLog();
        log.setBookingId(bookingId);
        log.setOldStatus(oldStatus == null ? null : oldStatus.name());
        log.setNewStatus(newStatus == null ? null : newStatus.name());
        log.setOperatorUsername(operatorUsername);
        log.setRemark(remark);
        bookingAuditLogRepository.save(log);
    }
}
