package com.cpt202.booking.specialist;

import com.cpt202.booking.enums.GenderType;
import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.model.User;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.repository.UserRepository;
import com.cpt202.booking.service.SpecialistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    void createSpecialistRejectsTrimmedDuplicateNameAndCategory() {
        ExpertiseCategory category = categoryRepository.findByNameIgnoreCase("Legal").orElseThrow();

        assertThrows(IllegalArgumentException.class, () -> specialistService.createSpecialist(
                "  Alice Chen  ",
                "Senior",
                300.0,
                "Duplicate specialist",
                category.getId()
        ));
    }

    @Test
    void createSpecialistRejectsOverlongDescription() {
        ExpertiseCategory category = categoryRepository.findByNameIgnoreCase("Legal").orElseThrow();

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> specialistService.createSpecialist(
                "Length Checked Specialist",
                "Senior",
                300.0,
                "a".repeat(256),
                category.getId()
        ));

        assertEquals("Academic expert description must not exceed 255 characters.", error.getMessage());
    }

    @Test
    void adminCanDeleteUnusedSpecialistAndRelatedAccountData() {
        ExpertiseCategory category = categoryRepository.findByNameIgnoreCase("Legal").orElseThrow();
        Specialist specialist = specialistRepository.save(new Specialist(
                "Disposable Specialist",
                "Associate",
                180.0,
                "No appointment history.",
                SpecialistStatus.PENDING_APPROVAL,
                category
        ));
        Long specialistId = specialist.getId();

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setSpecialist(specialist);
        slot.setSlotDate(LocalDate.now().plusDays(3));
        slot.setStartTime(LocalTime.of(9, 0));
        slot.setEndTime(LocalTime.of(10, 0));
        slot.setBooked(false);
        availabilitySlotRepository.save(slot);

        User user = new User("delete-specialist", "encoded", "Disposable Specialist",
                "delete-specialist@example.com", "13800009999", GenderType.FEMALE, RoleType.SPECIALIST);
        user.setSpecialistId(specialistId);
        userRepository.save(user);

        specialistService.deleteSpecialist(specialistId);

        assertTrue(specialistRepository.findById(specialistId).isEmpty());
        assertTrue(availabilitySlotRepository.findBySpecialistIdOrderBySlotDateAscStartTimeAsc(specialistId).isEmpty());
        assertTrue(userRepository.findBySpecialistId(specialistId).isEmpty());
    }

    @Test
    void customerSearchByDateExcludesSpecialistsWithOnlyPastSlotsToday() {
        ExpertiseCategory category = categoryRepository.findByNameIgnoreCase("Legal").orElseThrow();

        Specialist specialist = new Specialist(
                "Past Slot Specialist",
                "Consultant",
                220.0,
                "Only has expired slots today.",
                SpecialistStatus.ACTIVE,
                category
        );
        specialist = specialistRepository.save(specialist);
        Long specialistId = specialist.getId();

        LocalTime pastStart = LocalTime.now().minusHours(2).withSecond(0).withNano(0);
        LocalTime pastEnd = pastStart.plusHours(1);

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setSpecialist(specialist);
        slot.setSlotDate(LocalDate.now());
        slot.setStartTime(pastStart);
        slot.setEndTime(pastEnd);
        slot.setBooked(false);
        availabilitySlotRepository.save(slot);

        List<Specialist> specialists = specialistService.searchSpecialists("Past Slot Specialist", category.getId(), LocalDate.now());

        assertTrue(specialists.stream().noneMatch(item -> item.getId().equals(specialistId)));
    }
}
