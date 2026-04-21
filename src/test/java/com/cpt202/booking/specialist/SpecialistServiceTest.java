package com.cpt202.booking.specialist;

import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.service.SpecialistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SpecialistServiceTest {

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private ExpertiseCategoryRepository categoryRepository;

    @Test
    void customerSearchFiltersByCategoryAndAvailability() {
        ExpertiseCategory category = categoryRepository.findByNameIgnoreCase("Legal").orElseThrow();
        List<Specialist> specialists = specialistService.searchSpecialists("", category.getId(), LocalDate.now().plusDays(1));

        assertFalse(specialists.isEmpty());
        assertTrue(specialists.stream().allMatch(specialist -> specialist.getCategory() != null && category.getId().equals(specialist.getCategory().getId())));
    }

    @Test
    void adminSearchCanFilterInactiveSpecialists() {
        Specialist specialist = specialistRepository.findAll().get(0);
        specialist.setStatus(SpecialistStatus.INACTIVE);
        specialistRepository.save(specialist);

        List<Specialist> specialists = specialistService.searchSpecialistsForAdmin("", null, SpecialistStatus.INACTIVE);

        assertTrue(specialists.stream().anyMatch(item -> item.getId().equals(specialist.getId())));
    }
}
