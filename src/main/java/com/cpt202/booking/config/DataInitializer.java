package com.cpt202.booking.config;

import com.cpt202.booking.enums.CategoryStatus;
import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.AvailabilitySlotRepository;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(ExpertiseCategoryRepository categoryRepository,
                               SpecialistRepository specialistRepository,
                               AvailabilitySlotRepository slotRepository) {
        return args -> {
            if (categoryRepository.count() > 0 || specialistRepository.count() > 0) {
                return;
            }

            ExpertiseCategory legal = categoryRepository.save(new ExpertiseCategory("Legal", CategoryStatus.ACTIVE));
            ExpertiseCategory finance = categoryRepository.save(new ExpertiseCategory("Finance", CategoryStatus.ACTIVE));

            Specialist alice = new Specialist("Alice Chen", "Senior", 300.0, "Commercial law and contract specialist.", SpecialistStatus.ACTIVE, legal);
            Specialist bob = new Specialist("Bob Wang", "Consultant", 260.0, "SME finance and tax planning expert.", SpecialistStatus.ACTIVE, finance);
            alice = specialistRepository.save(alice);
            bob = specialistRepository.save(bob);

            slotRepository.save(createSlot(alice, 1, 10, 0, 11, 0));
            slotRepository.save(createSlot(alice, 2, 14, 0, 15, 0));
            slotRepository.save(createSlot(bob, 1, 9, 0, 10, 0));
            slotRepository.save(createSlot(bob, 3, 16, 0, 17, 0));
        };
    }

    private AvailabilitySlot createSlot(Specialist specialist, int daysLater, int startHour, int startMinute, int endHour, int endMinute) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setSpecialist(specialist);
        slot.setSlotDate(LocalDate.now().plusDays(daysLater));
        slot.setStartTime(LocalTime.of(startHour, startMinute));
        slot.setEndTime(LocalTime.of(endHour, endMinute));
        slot.setBooked(false);
        return slot;
    }
}
