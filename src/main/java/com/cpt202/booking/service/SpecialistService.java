package com.cpt202.booking.service;

import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.stereotype.Service;

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

    public Specialist getSpecialistById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found."));
    }

    private boolean hasAvailableSlotOnDate(Long specialistId, LocalDate date) {
        List<AvailabilitySlot> slots = availabilitySlotRepository.findBySpecialistIdAndBookedFalseAndSlotDateOrderByStartTimeAsc(specialistId, date);
        return !slots.isEmpty();
    }

    public Specialist createSpecialist(String name, String level, Double feeRate, String description, Long categoryId) {
        ExpertiseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));

        if (specialistRepository.existsByNameIgnoreCaseAndCategoryId(name, categoryId)) {
            throw new IllegalArgumentException("Duplicate specialist name and category combination.");
        }

        Specialist specialist = new Specialist();
        specialist.setName(name);
        specialist.setLevel(level);
        specialist.setFeeRate(feeRate);
        specialist.setProfileDescription(description);
        specialist.setStatus(SpecialistStatus.ACTIVE);
        specialist.setCategory(category);
        return specialistRepository.save(specialist);
    }

    public Specialist updateSpecialist(Long id, String name, String level, Double feeRate, String description, Long categoryId) {
        Specialist specialist = getSpecialistById(id);
        ExpertiseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));

        if (specialistRepository.existsByNameIgnoreCaseAndCategoryId(name, categoryId)
                && (!specialist.getName().equalsIgnoreCase(name) || specialist.getCategory() == null || !specialist.getCategory().getId().equals(categoryId))) {
            throw new IllegalArgumentException("Duplicate specialist name and category combination.");
        }

        specialist.setName(name);
        specialist.setLevel(level);
        specialist.setFeeRate(feeRate);
        specialist.setProfileDescription(description);
        specialist.setCategory(category);
        return specialistRepository.save(specialist);
    }

    public Specialist toggleStatus(Long id) {
        Specialist specialist = getSpecialistById(id);
        specialist.setStatus(specialist.getStatus() == SpecialistStatus.ACTIVE ? SpecialistStatus.INACTIVE : SpecialistStatus.ACTIVE);
        return specialistRepository.save(specialist);
    }
}
