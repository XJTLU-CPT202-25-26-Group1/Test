package com.cpt202.booking.service;

import com.cpt202.booking.enums.CategoryStatus;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpertiseCategoryService {

    private final ExpertiseCategoryRepository expertiseCategoryRepository;

    public ExpertiseCategoryService(ExpertiseCategoryRepository expertiseCategoryRepository) {
        this.expertiseCategoryRepository = expertiseCategoryRepository;
    }

    public List<ExpertiseCategory> getAllCategories() {
        return expertiseCategoryRepository.findAll();
    }

    public ExpertiseCategory createCategory(String name) {
        expertiseCategoryRepository.findByNameIgnoreCase(name)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Category already exists.");
                });

        return expertiseCategoryRepository.save(new ExpertiseCategory(name, CategoryStatus.ACTIVE));
    }
}
