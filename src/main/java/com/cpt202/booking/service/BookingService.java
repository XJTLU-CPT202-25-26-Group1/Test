package com.cpt202.booking.service;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.BookingAuditLog;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.BookingAuditLogRepository;
import com.cpt202.booking.repository.BookingRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService {

    static final int BOOKING_TOPIC_MAX_LENGTH = 255;
    static final int BOOKING_NOTES_MAX_LENGTH = 255;
    static final int REJECTION_REASON_MAX_LENGTH = 255;
    static final int AUDIT_REMARK_MAX_LENGTH = 255;

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

    public Booking getBookingDetail(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));
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

    public List<Booking> getSpecialistBookingsForWeek(Long specialistId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return bookingRepository.findBySpecialistIdOrderByCreatedAtDesc(specialistId)
                .stream()
                .filter(booking -> {
                    LocalDate slotDate = booking.getSlot().getSlotDate();
                    return (slotDate.isEqual(weekStart) || slotDate.isAfter(weekStart))
                            && (slotDate.isEqual(weekEnd) || slotDate.isBefore(weekEnd));
                })
                .sorted(Comparator.comparing((Booking booking) -> booking.getSlot().getSlotDate())
                        .thenComparing(booking -> booking.getSlot().getStartTime()))
                .collect(Collectors.toList());
    }

    public List<Booking> getSpecialistCompletedBookings(Long specialistId) {
        return bookingRepository.findBySpecialistIdOrderByCreatedAtDesc(specialistId)
                .stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    public Booking getCustomerBookingDetail(Long bookingId, String customerEmail) {
        return bookingRepository.findByIdAndCustomerEmail(bookingId, customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));
    }

    public Booking getSpecialistBookingDetail(Long bookingId, Long specialistId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));
        if (!booking.getSpecialist().getId().equals(specialistId)) {
            throw new IllegalArgumentException("You can only view your own consultation appointments.");
        }
        return booking;
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

    public List<BookingAuditLog> getAuditLogsForBooking(Long bookingId) {
        return bookingAuditLogRepository.findByBookingIdOrderByOperatedAtDesc(bookingId);
    }

    public List<BookingAuditLog> getCustomerNotifications(String customerEmail) {
        Set<Long> bookingIds = bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail)
                .stream()
                .map(Booking::getId)
                .collect(Collectors.toCollection(HashSet::new));
        return bookingAuditLogRepository.findAllByOrderByOperatedAtDesc()
                .stream()
                .filter(log -> bookingIds.contains(log.getBookingId()))
                .limit(3)
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
                .limit(3)
                .collect(Collectors.toList());
    }

    @Transactional
    public Booking createBooking(String customerName, String customerEmail, Long specialistId, Long slotId, String topic, String notes) {
        String normalizedTopic = normalizeRequiredText(topic, "Consultation topic", BOOKING_TOPIC_MAX_LENGTH);
        String normalizedNotes = normalizeOptionalText(notes, "Appointment notes", BOOKING_NOTES_MAX_LENGTH);

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Academic expert not found."));
        if (specialist.getStatus() != SpecialistStatus.ACTIVE) {
            throw new IllegalStateException("Selected academic expert is not available for appointments.");
        }

        AvailabilitySlot slot = availabilitySlotRepository.findByIdForUpdate(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found."));

        if (!slot.getSpecialist().getId().equals(specialistId)) {
            throw new IllegalArgumentException("Slot does not belong to this academic expert.");
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
        booking.setTopic(normalizedTopic);
        booking.setNotes(normalizedNotes);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCalculatedFee(calculateFee(specialist, slot));

        slot.setBooked(true);
        availabilitySlotRepository.save(slot);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), null, BookingStatus.PENDING, customerEmail, "Appointment request created");
        return saved;
    }

    @Transactional
    public Booking confirmBooking(Long id) {
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending appointment requests can be confirmed.");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.CONFIRMED, "admin", "Appointment request confirmed");
        return saved;
    }

    @Transactional
    public Booking rejectBooking(Long id, String reason) {
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));
        String normalizedReason = normalizeRequiredText(reason, "Rejection reason", REJECTION_REASON_MAX_LENGTH);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending appointment requests can be rejected.");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.REJECTED);
        booking.setRejectionReason(normalizedReason);
        releaseSlot(booking);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.REJECTED, "admin", "Appointment request rejected: " + normalizedReason);
        return saved;
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending or confirmed appointment requests can be cancelled.");
        }

        if (isWithin24Hours(booking)) {
            throw new IllegalStateException("Cancellation is not allowed within 24 hours.");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        releaseSlot(booking);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.CANCELLED, booking.getCustomerEmail(), "Appointment cancelled by requester");
        return saved;
    }

    @Transactional
    public Booking cancelBookingForCustomer(Long id, String customerEmail) {
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));
        if (!booking.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
            throw new IllegalArgumentException("You can only cancel your own appointment request.");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending or confirmed appointment requests can be cancelled.");
        }
        if (isWithin24Hours(booking)) {
            throw new IllegalStateException("Cancellation is not allowed within 24 hours.");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        releaseSlot(booking);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.CANCELLED, booking.getCustomerEmail(), "Appointment cancelled by requester");
        return saved;
    }

    @Transactional
    public Booking rescheduleBooking(Long bookingId, Long newSlotId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointment requests can be rescheduled.");
        }

        if (isWithin24Hours(booking)) {
            throw new IllegalStateException("Rescheduling is not allowed within 24 hours.");
        }

        AvailabilitySlot newSlot = availabilitySlotRepository.findByIdForUpdate(newSlotId)
                .orElseThrow(() -> new IllegalArgumentException("New slot not found."));
        validateRescheduleTarget(booking, newSlot);

        releaseSlot(booking);
        newSlot.setBooked(true);
        availabilitySlotRepository.save(newSlot);

        BookingStatus oldStatus = booking.getStatus();
        booking.setSlot(newSlot);
        booking.setSpecialist(newSlot.getSpecialist());
        booking.setCalculatedFee(calculateFee(newSlot.getSpecialist(), newSlot));
        booking.setStatus(BookingStatus.PENDING);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.PENDING, booking.getCustomerEmail(), "Appointment rescheduled and returned to pending review");
        return saved;
    }

    @Transactional
    public Booking rescheduleBookingForCustomer(Long bookingId, Long newSlotId, String customerEmail) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));
        if (!booking.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
            throw new IllegalArgumentException("You can only reschedule your own appointment request.");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointment requests can be rescheduled.");
        }
        if (isWithin24Hours(booking)) {
            throw new IllegalStateException("Rescheduling is not allowed within 24 hours.");
        }

        AvailabilitySlot newSlot = availabilitySlotRepository.findByIdForUpdate(newSlotId)
                .orElseThrow(() -> new IllegalArgumentException("New slot not found."));
        validateRescheduleTarget(booking, newSlot);

        releaseSlot(booking);
        newSlot.setBooked(true);
        availabilitySlotRepository.save(newSlot);

        BookingStatus oldStatus = booking.getStatus();
        booking.setSlot(newSlot);
        booking.setSpecialist(newSlot.getSpecialist());
        booking.setCalculatedFee(calculateFee(newSlot.getSpecialist(), newSlot));
        booking.setStatus(BookingStatus.PENDING);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.PENDING, booking.getCustomerEmail(), "Appointment rescheduled and returned to pending review");
        return saved;
    }

    @Transactional
    public Booking completeBooking(Long id) {
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointment requests can be completed.");
        }
        if (!hasConsultationFinished(booking)) {
            throw new IllegalStateException("Appointment can only be completed after the consultation has finished.");
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.COMPLETED);
        Booking saved = bookingRepository.save(booking);
        createAuditLog(saved.getId(), oldStatus, BookingStatus.COMPLETED, "expert", "Consultation marked completed");
        return saved;
    }

    @Transactional
    public Booking completeBookingForSpecialist(Long id, Long specialistId) {
        Booking booking = bookingRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment request not found."));
        if (!booking.getSpecialist().getId().equals(specialistId)) {
            throw new IllegalArgumentException("You can only complete your own consultation appointments.");
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

    private boolean hasConsultationFinished(Booking booking) {
        LocalDateTime consultationEnd = LocalDateTime.of(
                booking.getSlot().getSlotDate(),
                booking.getSlot().getEndTime()
        );
        return !consultationEnd.isAfter(LocalDateTime.now());
    }

    private void validateRescheduleTarget(Booking booking, AvailabilitySlot newSlot) {
        if (!newSlot.getSpecialist().getId().equals(booking.getSpecialist().getId())) {
            throw new IllegalStateException("Appointment can only be rescheduled to another slot for the same academic expert.");
        }
        if (newSlot.isBooked()) {
            throw new IllegalStateException("New slot is already booked.");
        }
        if (newSlot.getSpecialist().getStatus() != SpecialistStatus.ACTIVE) {
            throw new IllegalStateException("New slot belongs to an inactive academic expert.");
        }
        if (newSlot.getSlotDate() == null || newSlot.getStartTime() == null
                || LocalDateTime.of(newSlot.getSlotDate(), newSlot.getStartTime()).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("New slot must be in the future.");
        }
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
        log.setRemark(truncateText(remark, AUDIT_REMARK_MAX_LENGTH));
        bookingAuditLogRepository.save(log);
    }

    private String normalizeRequiredText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters.");
        }
        return normalized;
    }

    private String normalizeOptionalText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters.");
        }
        return normalized;
    }

    private String truncateText(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
