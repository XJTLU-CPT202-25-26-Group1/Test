package com.cpt202.booking.config;

import com.cpt202.booking.enums.CategoryStatus;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@Profile({"dev", "test"})
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(ExpertiseCategoryRepository categoryRepository,
                               SpecialistRepository specialistRepository,
                               AvailabilitySlotRepository slotRepository,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            ExpertiseCategory legal = categoryRepository.findByNameIgnoreCase("Legal")
                    .orElseGet(() -> categoryRepository.save(new ExpertiseCategory("Legal", CategoryStatus.ACTIVE)));
            ExpertiseCategory finance = categoryRepository.findByNameIgnoreCase("Finance")
                    .orElseGet(() -> categoryRepository.save(new ExpertiseCategory("Finance", CategoryStatus.ACTIVE)));

            Specialist alice = specialistRepository.findAll().stream()
                    .filter(specialist -> "Alice Chen".equalsIgnoreCase(specialist.getName()))
                    .findFirst()
                    .orElseGet(() -> specialistRepository.save(
                            new Specialist("Alice Chen", "Senior", 300.0, "XJTLU academic support expert for contract and commercial law consultations.", SpecialistStatus.ACTIVE, legal)
                    ));

            Specialist bob = specialistRepository.findAll().stream()
                    .filter(specialist -> "Bob Wang".equalsIgnoreCase(specialist.getName()))
                    .findFirst()
                    .orElseGet(() -> specialistRepository.save(
                            new Specialist("Bob Wang", "Consultant", 260.0, "XJTLU academic support expert for finance, budgeting, and tax planning consultations.", SpecialistStatus.ACTIVE, finance)
                    ));

            if (slotRepository.count() == 0) {
                slotRepository.save(createSlot(alice, 1, 10, 0, 11, 0));
                slotRepository.save(createSlot(alice, 2, 14, 0, 15, 0));
                slotRepository.save(createSlot(bob, 1, 9, 0, 10, 0));
                slotRepository.save(createSlot(bob, 3, 16, 0, 17, 0));
            }

            seedUser(userRepository, passwordEncoder, "admin", "admin123", "CPT202 Admin", "admin@xjtlu.local", "13800000001", GenderType.MALE, RoleType.ADMIN, null);
            seedUser(userRepository, passwordEncoder, "specialist", "specialist123", "CPT202 Expert", "expert@xjtlu.local", "13800000002", GenderType.MALE, RoleType.SPECIALIST, alice.getId());
            seedUser(userRepository, passwordEncoder, "customer", "customer123", "CPT202 Requester", "requester@xjtlu.local", "13800000003", GenderType.FEMALE, RoleType.CUSTOMER, null);
        };
    }

    private void seedUser(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          String username,
                          String rawPassword,
                          String displayName,
                          String email,
                          String phone,
                          GenderType gender,
                          RoleType role,
                          Long specialistId) {
        User existing = userRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (existing != null) {
            existing.setDisplayName(displayName);
            existing.setEmail(email);
            existing.setPhone(phone);
            existing.setGender(gender);
            existing.setRole(role);
            existing.setSpecialistId(specialistId);
            existing.setEmailVerified(true);
            existing.setVerificationToken(null);
            if (!passwordEncoder.matches(rawPassword, existing.getPassword())) {
                existing.setPassword(passwordEncoder.encode(rawPassword));
            }
            userRepository.save(existing);
            return;
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), displayName, email, phone, gender, role);
        user.setSpecialistId(specialistId);
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
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
