package com.cpt202.booking.category;

import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.service.ExpertiseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpertiseCategoryServiceTest {

    @Autowired
    private ExpertiseCategoryService categoryService;

    @Autowired
    private ExpertiseCategoryRepository categoryRepository;

    @Test
    void createCategoryRejectsBlankName() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory("   "));

        assertEquals("Category name is required.", error.getMessage());
    }

    @Test
    void createCategoryRejectsTrimmedDuplicateName() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory("  Legal  "));

        assertEquals("Category already exists.", error.getMessage());
    }

    @Test
    void updateCategoryStoresTrimmedName() {
        ExpertiseCategory category = categoryRepository.findByNameIgnoreCase("Finance").orElseThrow();

        ExpertiseCategory updated = categoryService.updateCategory(category.getId(), "  Tax Planning  ");

        assertEquals("Tax Planning", updated.getName());
    }
}
