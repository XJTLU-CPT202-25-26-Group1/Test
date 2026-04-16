package com.cpt202.booking.service;

import com.cpt202.booking.enums.CategoryStatus;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpertiseCategoryService {

    private final ExpertiseCategoryRepository expertiseCategoryRepository;

    public ExpertiseCategoryService(ExpertiseCategoryRepository expertiseCategoryRepository) {
        this.expertiseCategoryRepository = expertiseCategoryRepository;
    }

    public List<ExpertiseCategory> getAllCategories() {
        return expertiseCategoryRepository.findAll();
    }

    public List<ExpertiseCategory> getActiveCategories() {
        return expertiseCategoryRepository.findAll()
                .stream()
                .filter(category -> category.getStatus() == CategoryStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public ExpertiseCategory createCategory(String name) {
        expertiseCategoryRepository.findByNameIgnoreCase(name)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Category already exists.");
                });

        return expertiseCategoryRepository.save(new ExpertiseCategory(name, CategoryStatus.ACTIVE));
    }

    public ExpertiseCategory updateCategory(Long id, String name) {
        ExpertiseCategory category = expertiseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));

        expertiseCategoryRepository.findByNameIgnoreCase(name)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Category already exists.");
                });

        category.setName(name);
        return expertiseCategoryRepository.save(category);
    }

    public ExpertiseCategory toggleCategoryStatus(Long id) {
        ExpertiseCategory category = expertiseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));
        category.setStatus(category.getStatus() == CategoryStatus.ACTIVE ? CategoryStatus.INACTIVE : CategoryStatus.ACTIVE);
        return expertiseCategoryRepository.save(category);
    }
}
