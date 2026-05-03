package com.cpt202.booking.service;

import com.cpt202.booking.enums.CategoryStatus;
import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.model.User;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.BookingRepository;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.repository.FeedbackRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialistService {

    static final int SPECIALIST_NAME_MAX_LENGTH = 255;
    static final int SPECIALIST_LEVEL_MAX_LENGTH = 255;
    static final int SPECIALIST_DESCRIPTION_MAX_LENGTH = 255;

    private final SpecialistRepository specialistRepository;
    private final ExpertiseCategoryRepository categoryRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final BookingRepository bookingRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AvatarStorageService avatarStorageService;

    public SpecialistService(SpecialistRepository specialistRepository,
                             ExpertiseCategoryRepository categoryRepository,
                             AvailabilitySlotRepository availabilitySlotRepository,
                             BookingRepository bookingRepository,
                             FeedbackRepository feedbackRepository,
                             UserRepository userRepository,
                             EmailService emailService,
                             AvatarStorageService avatarStorageService) {
        this.specialistRepository = specialistRepository;
        this.categoryRepository = categoryRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.bookingRepository = bookingRepository;
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.avatarStorageService = avatarStorageService;
    }

    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }

    public List<Specialist> searchSpecialists(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return specialistRepository.findAll();
        }
        return specialistRepository.findByNameContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(keyword, keyword);
    }

    public List<Specialist> searchSpecialists(String keyword, Long categoryId, LocalDate availableDate) {
        return specialistRepository.findAll()
                .stream()
                .filter(specialist -> specialist.getStatus() == SpecialistStatus.ACTIVE)
                .filter(specialist -> keyword == null || keyword.isBlank()
                        || specialist.getName().toLowerCase().contains(keyword.toLowerCase())
                        || (specialist.getCategory() != null
                        && specialist.getCategory().getName().toLowerCase().contains(keyword.toLowerCase())))
                .filter(specialist -> categoryId == null
                        || (specialist.getCategory() != null && categoryId.equals(specialist.getCategory().getId())))
                .filter(specialist -> availableDate == null || hasAvailableSlotOnDate(specialist.getId(), availableDate))
                .collect(Collectors.toList());
    }

    public List<Specialist> searchSpecialistsForAdmin(String keyword, Long categoryId, SpecialistStatus status) {
        return specialistRepository.findAll()
                .stream()
                .filter(specialist -> keyword == null || keyword.isBlank()
                        || specialist.getName().toLowerCase().contains(keyword.toLowerCase())
                        || (specialist.getCategory() != null
                        && specialist.getCategory().getName().toLowerCase().contains(keyword.toLowerCase())))
                .filter(specialist -> categoryId == null
                        || (specialist.getCategory() != null && categoryId.equals(specialist.getCategory().getId())))
                .filter(specialist -> status == null || specialist.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Specialist getSpecialistById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Academic expert not found."));
    }

    private boolean hasAvailableSlotOnDate(Long specialistId, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        List<AvailabilitySlot> slots = availabilitySlotRepository.findBySpecialistIdAndBookedFalseAndSlotDateOrderByStartTimeAsc(specialistId, date);
        return slots.stream().anyMatch(slot -> isSlotInFuture(slot, now));
    }

    private boolean isSlotInFuture(AvailabilitySlot slot, LocalDateTime now) {
        return slot.getSlotDate() != null
                && slot.getStartTime() != null
                && LocalDateTime.of(slot.getSlotDate(), slot.getStartTime()).isAfter(now);
    }

    @Transactional
    public Specialist createSpecialist(String name, String level, Double feeRate, String description, Long categoryId) {
        return createSpecialist(name, level, feeRate, description, categoryId, SpecialistStatus.ACTIVE);
    }

    @Transactional
    public Specialist createPendingSpecialist(String name, String level, Double feeRate, String description, Long categoryId) {
        return createSpecialist(name, level, feeRate, description, categoryId, SpecialistStatus.PENDING_APPROVAL);
    }

    @Transactional
    public Specialist createSpecialist(String name,
                                       String level,
                                       Double feeRate,
                                       String description,
                                       Long categoryId,
                                       SpecialistStatus initialStatus) {
        String normalizedName = normalizeRequiredText(name, "Academic expert name", SPECIALIST_NAME_MAX_LENGTH);
        String normalizedLevel = normalizeRequiredText(level, "Academic expert level", SPECIALIST_LEVEL_MAX_LENGTH);
        String normalizedDescription = normalizeRequiredText(description, "Academic expert description", SPECIALIST_DESCRIPTION_MAX_LENGTH);
        double normalizedFeeRate = normalizeFeeRate(feeRate);
        ExpertiseCategory category = requireActiveCategory(categoryId);

        if (specialistRepository.existsByNameIgnoreCaseAndCategoryId(normalizedName, categoryId)) {
            throw new IllegalArgumentException("Duplicate academic expert name and academic area combination.");
        }

        Specialist specialist = new Specialist();
        specialist.setName(normalizedName);
        specialist.setLevel(normalizedLevel);
        specialist.setFeeRate(normalizedFeeRate);
        specialist.setProfileDescription(normalizedDescription);
        specialist.setStatus(initialStatus == null ? SpecialistStatus.ACTIVE : initialStatus);
        specialist.setCategory(category);
        return specialistRepository.save(specialist);
    }

    @Transactional
    public Specialist updateSpecialist(Long id, String name, String level, Double feeRate, String description, Long categoryId) {
        Specialist specialist = getSpecialistById(id);
        String normalizedName = normalizeRequiredText(name, "Academic expert name", SPECIALIST_NAME_MAX_LENGTH);
        String normalizedLevel = normalizeRequiredText(level, "Academic expert level", SPECIALIST_LEVEL_MAX_LENGTH);
        String normalizedDescription = normalizeRequiredText(description, "Academic expert description", SPECIALIST_DESCRIPTION_MAX_LENGTH);
        double normalizedFeeRate = normalizeFeeRate(feeRate);
        ExpertiseCategory category = requireActiveCategory(categoryId);

        if (specialistRepository.existsByNameIgnoreCaseAndCategoryId(normalizedName, categoryId)
                && (!specialist.getName().equalsIgnoreCase(normalizedName) || specialist.getCategory() == null || !specialist.getCategory().getId().equals(categoryId))) {
            throw new IllegalArgumentException("Duplicate academic expert name and academic area combination.");
        }

        specialist.setName(normalizedName);
        specialist.setLevel(normalizedLevel);
        specialist.setFeeRate(normalizedFeeRate);
        specialist.setProfileDescription(normalizedDescription);
        specialist.setCategory(category);
        return specialistRepository.save(specialist);
    }

    @Transactional
    public Specialist toggleStatus(Long id) {
        Specialist specialist = getSpecialistById(id);
        if (specialist.getStatus() == SpecialistStatus.PENDING_APPROVAL || specialist.getStatus() == SpecialistStatus.REJECTED) {
            throw new IllegalStateException("Pending or rejected academic experts must be approved before active status can be changed.");
        }
        specialist.setStatus(specialist.getStatus() == SpecialistStatus.ACTIVE ? SpecialistStatus.INACTIVE : SpecialistStatus.ACTIVE);
        return specialistRepository.save(specialist);
    }

    @Transactional
    public Specialist approveSpecialist(Long id) {
        Specialist specialist = getSpecialistById(id);
        if (specialist.getStatus() != SpecialistStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending academic expert registrations can be approved.");
        }
        specialist.setStatus(SpecialistStatus.ACTIVE);
        Specialist saved = specialistRepository.save(specialist);
        userRepository.findBySpecialistId(saved.getId())
                .ifPresent(user -> emailService.sendSpecialistApprovalEmail(user, user.isEmailVerified()));
        return saved;
    }

    @Transactional
    public Specialist rejectSpecialist(Long id) {
        Specialist specialist = getSpecialistById(id);
        if (specialist.getStatus() != SpecialistStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending academic expert registrations can be rejected.");
        }
        specialist.setStatus(SpecialistStatus.REJECTED);
        Specialist saved = specialistRepository.save(specialist);
        userRepository.findBySpecialistId(saved.getId())
                .ifPresent(emailService::sendSpecialistRejectionEmail);
        return saved;
    }

    @Transactional
    public void deleteSpecialist(Long id) {
        Specialist specialist = specialistRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Academic expert not found."));
        if (bookingRepository.existsBySpecialistId(id)) {
            throw new IllegalStateException("Academic experts with appointment history cannot be deleted. Deactivate them instead.");
        }
        if (feedbackRepository.existsBySpecialistId(id)) {
            throw new IllegalStateException("Academic experts with feedback history cannot be deleted. Deactivate them instead.");
        }
        if (availabilitySlotRepository.existsBySpecialistIdAndBookedTrue(id)) {
            throw new IllegalStateException("Academic experts with booked slots cannot be deleted. Deactivate them instead.");
        }

        User linkedUser = userRepository.findBySpecialistId(id).orElse(null);
        String avatarPath = linkedUser == null ? null : linkedUser.getAvatarPath();

        availabilitySlotRepository.deleteBySpecialistId(id);
        if (linkedUser != null) {
            userRepository.delete(linkedUser);
        }
        specialistRepository.delete(specialist);
        avatarStorageService.deleteAvatar(avatarPath);
    }

    private ExpertiseCategory requireActiveCategory(Long categoryId) {
        ExpertiseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Academic area not found."));
        if (category.getStatus() != CategoryStatus.ACTIVE) {
            throw new IllegalArgumentException("Inactive academic area cannot be assigned to an academic expert.");
        }
        return category;
    }

    private String normalizeRequiredText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " is too long. Please shorten it.");
        }
        return normalized;
    }

    private double normalizeFeeRate(Double feeRate) {
        if (feeRate == null || feeRate <= 0) {
            throw new IllegalArgumentException("Fee rate must be greater than 0.");
        }
        return feeRate;
    }
}
