package com.cpt202.booking.service;

import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final ExpertiseCategoryRepository categoryRepository;

    public SpecialistService(SpecialistRepository specialistRepository, ExpertiseCategoryRepository categoryRepository) {
        this.specialistRepository = specialistRepository;
        this.categoryRepository = categoryRepository;
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

    public Specialist createSpecialist(String name, String level, Double feeRate, String description, Long categoryId) {
        ExpertiseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));

        Specialist specialist = new Specialist();
        specialist.setName(name);
        specialist.setLevel(level);
        specialist.setFeeRate(feeRate);
        specialist.setProfileDescription(description);
        specialist.setStatus(SpecialistStatus.ACTIVE);
        specialist.setCategory(category);
        return specialistRepository.save(specialist);
    }
}
