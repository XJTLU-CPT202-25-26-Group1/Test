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
        String normalizedName = normalizeName(name);

        expertiseCategoryRepository.findByNameIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Category already exists.");
                });

        return expertiseCategoryRepository.save(new ExpertiseCategory(normalizedName, CategoryStatus.ACTIVE));
    }

    public ExpertiseCategory updateCategory(Long id, String name) {
        ExpertiseCategory category = expertiseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));
        String normalizedName = normalizeName(name);

        expertiseCategoryRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Category already exists.");
                });

        category.setName(normalizedName);
        return expertiseCategoryRepository.save(category);
    }

    public ExpertiseCategory toggleCategoryStatus(Long id) {
        ExpertiseCategory category = expertiseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found."));
        category.setStatus(category.getStatus() == CategoryStatus.ACTIVE ? CategoryStatus.INACTIVE : CategoryStatus.ACTIVE);
        return expertiseCategoryRepository.save(category);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        return name.trim();
    }
}
