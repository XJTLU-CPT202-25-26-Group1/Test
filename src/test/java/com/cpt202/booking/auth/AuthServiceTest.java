package com.cpt202.booking.auth;

import com.cpt202.booking.enums.GenderType;
import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.User;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.repository.ExpertiseCategoryRepository;
import com.cpt202.booking.repository.UserRepository;
import com.cpt202.booking.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpertiseCategoryRepository categoryRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerUserPersistsEncodedPassword() {
        User created = userService.registerUser(
                "newcustomer",
                "password123",
                "New Customer",
                "newcustomer@example.com",
                "13800001111",
                GenderType.MALE,
                RoleType.CUSTOMER,
                null,
                null,
                null,
                null,
                null
        );

        User stored = userRepository.findByUsernameIgnoreCase("newcustomer").orElseThrow();
        assertEquals(created.getUsername(), stored.getUsername());
        assertNotEquals("password123", stored.getPassword());
        assertTrue(passwordEncoder.matches("password123", stored.getPassword()));
        assertFalse(stored.isEmailVerified());
        assertNotNull(stored.getVerificationToken());
    }

    @Test
    void specialistRegistrationCreatesLinkedSpecialistProfile() {
        Long categoryId = categoryRepository.findAll().get(0).getId();

        User created = userService.registerUser(
                "newspecialist",
                "password123",
                "New Specialist",
                "newspecialist@example.com",
                "13800002222",
                GenderType.FEMALE,
                RoleType.SPECIALIST,
                categoryId,
                "Senior",
                320.0,
                "Focused on complex consultation work.",
                null
        );

        assertNotNull(created.getSpecialistId());
        assertFalse(created.isEmailVerified());
        assertEquals(SpecialistStatus.PENDING_APPROVAL,
                specialistRepository.findById(created.getSpecialistId()).orElseThrow().getStatus());
    }

    @Test
    void resetPasswordUpdatesStoredPassword() {
        String token = userService.createResetToken("customer", "customer@demo.local");
        userService.resetPassword("customer", token, "newpass123");

        User stored = userRepository.findByUsernameIgnoreCase("customer").orElseThrow();
        assertNull(stored.getResetToken());
        assertTrue(passwordEncoder.matches("newpass123", stored.getPassword()));
    }

    @Test
    void resendVerificationRejectsAlreadyVerifiedUser() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> userService.resendVerificationToken("customer", "customer@demo.local"));

        assertEquals("Email is already verified.", error.getMessage());
        assertTrue(userRepository.findByUsernameIgnoreCase("customer").orElseThrow().isEmailVerified());
    }

    @Test
    void resolveSpecialistIdRejectsUnlinkedSpecialistAccount() {
        User user = new User("orphan-specialist", passwordEncoder.encode("password123"), "Orphan Specialist",
                "orphan-specialist@example.com", "13800003333", GenderType.MALE, RoleType.SPECIALIST);
        user.setEmailVerified(true);
        userRepository.save(user);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> userService.resolveSpecialistId("orphan-specialist"));

        assertEquals("Specialist account is not linked to a specialist profile.", error.getMessage());
    }

    @Test
    void specialistLoginBlockReasonReflectsApprovalStatus() {
        Long categoryId = categoryRepository.findAll().get(0).getId();

        User created = userService.registerUser(
                "pending-specialist",
                "password123",
                "Pending Specialist",
                "pending-specialist@example.com",
                "13800004444",
                GenderType.MALE,
                RoleType.SPECIALIST,
                categoryId,
                "Associate",
                220.0,
                "Pending review",
                null
        );
        created.setEmailVerified(true);
        userRepository.save(created);

        assertEquals("pendingApproval", userService.resolveLoginBlockReason("pending-specialist"));
    }
}
