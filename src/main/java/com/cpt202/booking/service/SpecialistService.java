package com.cpt202.booking.service;

import com.cpt202.booking.enums.CategoryStatus;
import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final ExpertiseCategoryRepository categoryRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public SpecialistService(SpecialistRepository specialistRepository,
                             ExpertiseCategoryRepository categoryRepository,
                             AvailabilitySlotRepository availabilitySlotRepository) {
        this.specialistRepository = specialistRepository;
        this.categoryRepository = categoryRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found."));
    }

    private boolean hasAvailableSlotOnDate(Long specialistId, LocalDate date) {
        List<AvailabilitySlot> slots = availabilitySlotRepository.findBySpecialistIdAndBookedFalseAndSlotDateOrderByStartTimeAsc(specialistId, date);
        return !slots.isEmpty();
    }

    @Transactional
    public Specialist createSpecialist(String name, String level, Double feeRate, String description, Long categoryId) {
        String normalizedName = normalizeRequiredText(name, "Specialist name");
        String normalizedLevel = normalizeRequiredText(level, "Specialist level");
        String normalizedDescription = normalizeRequiredText(description, "Specialist description");
        double normalizedFeeRate = normalizeFeeRate(feeRate);
        ExpertiseCategory category = requireActiveCategory(categoryId);

        if (specialistRepository.existsByNameIgnoreCaseAndCategoryId(normalizedName, categoryId)) {
            throw new IllegalArgumentException("Duplicate specialist name and category combination.");
        }

        Specialist specialist = new Specialist();
        specialist.setName(normalizedName);
        specialist.setLevel(normalizedLevel);
        specialist.setFeeRate(normalizedFeeRate);
        specialist.setProfileDescription(normalizedDescription);
        specialist.setStatus(SpecialistStatus.ACTIVE);
        specialist.setCategory(category);
        return specialistRepository.save(specialist);
    }

    @Transactional
    public Specialist updateSpecialist(Long id, String name, String level, Double feeRate, String description, Long categoryId) {
        Specialist specialist = getSpecialistById(id);
        String normalizedName = normalizeRequiredText(name, "Specialist name");
        String normalizedLevel = normalizeRequiredText(level, "Specialist level");
        String normalizedDescription = normalizeRequiredText(description, "Specialist description");
        double normalizedFeeRate = normalizeFeeRate(feeRate);
        ExpertiseCategory category = requireActiveCategory(categoryId);

        if (specialistRepository.existsByNameIgnoreCaseAndCategoryId(normalizedName, categoryId)
                && (!specialist.getName().equalsIgnoreCase(normalizedName) || specialist.getCategory() == null || !specialist.getCategory().getId().equals(categoryId))) {
            throw new IllegalArgumentException("Duplicate specialist name and category combination.");
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
        specialist.setStatus(specialist.getStatus() == SpecialistStatus.ACTIVE ? SpecialistStatus.INACTIVE : SpecialistStatus.ACTIVE);
        return specialistRepository.save(specialist);
    }

    private ExpertiseCategory requireActiveCategory(Long categoryId) {
        ExpertiseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));
        if (category.getStatus() != CategoryStatus.ACTIVE) {
            throw new IllegalArgumentException("Inactive category cannot be assigned to a specialist.");
        }
        return category;
    }

    private String normalizeRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private double normalizeFeeRate(Double feeRate) {
        if (feeRate == null || feeRate <= 0) {
            throw new IllegalArgumentException("Fee rate must be greater than 0.");
        }
        return feeRate;
    }
}
