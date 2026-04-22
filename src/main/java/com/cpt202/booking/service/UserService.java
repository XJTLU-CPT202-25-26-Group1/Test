package com.cpt202.booking.service;

import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.model.User;
import com.cpt202.booking.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SpecialistService specialistService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       SpecialistService specialistService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.specialistService = specialistService;
        this.passwordEncoder = passwordEncoder;
    }

    public User getByUsername(String username) {
        return requireExistingUser(username);
    }

    public User updateProfile(String username, String displayName, String email, String phone) {
        String normalizedEmail = normalizeEmail(email);
        ensureEmailAvailable(normalizedEmail, username);
        User user = requireExistingUser(username);
        user.setDisplayName(normalizeRequiredText(displayName, "Display name"));
        user.setEmail(normalizedEmail);
        user.setPhone(normalizeRequiredText(phone, "Phone"));
        return userRepository.save(user);
    }

    public User registerUser(String username,
                             String password,
                             String displayName,
                             String email,
                             String phone,
                             RoleType role,
                             Long categoryId,
                             String level,
                             Double feeRate,
                             String description) {
        String normalizedUsername = normalizeUsername(username);
        String normalizedEmail = normalizeEmail(email);
        if (role == RoleType.ADMIN) {
            throw new IllegalArgumentException("Administrator accounts are pre-provisioned only.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name is required.");
        }
        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new IllegalArgumentException("Username is already in use.");
        }
        ensureEmailAvailable(normalizedEmail, null);

        User user = new User(
                normalizedUsername,
                passwordEncoder.encode(password),
                normalizeRequiredText(displayName, "Display name"),
                normalizedEmail,
                normalizeRequiredText(phone, "Phone"),
                role
        );
        user.setVerificationToken(generateToken());
        user.setEmailVerified(false);
        if (role == RoleType.SPECIALIST) {
            if (categoryId == null) {
                throw new IllegalArgumentException("Specialist registration requires an expertise category.");
            }
            Double finalFeeRate = feeRate == null ? 200.0 : feeRate;
            String finalLevel = (level == null || level.isBlank()) ? "Associate" : level;
            String finalDescription = (description == null || description.isBlank())
                    ? "Self-registered specialist profile pending enrichment."
                    : description.trim();
            Specialist specialist = specialistService.createSpecialist(displayName, finalLevel, finalFeeRate, finalDescription, categoryId);
            user.setSpecialistId(specialist.getId());
        }
        return userRepository.save(user);
    }

    public User verifyEmail(String username, String token) {
        User user = userRepository.findByUsernameIgnoreCaseAndVerificationToken(normalizeUsername(username), token)
                .orElseThrow(() -> new IllegalArgumentException("Verification link is invalid or expired."));
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        return userRepository.save(user);
    }

    public User resendVerificationToken(String username, String email) {
        User user = requireExistingUser(username);
        if (!user.getEmail().equalsIgnoreCase(normalizeEmail(email))) {
            throw new IllegalArgumentException("Username and email do not match.");
        }
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified.");
        }
        user.setVerificationToken(generateToken());
        return userRepository.save(user);
    }

    public String createResetToken(String username, String email) {
        User user = requireExistingUser(username);
        if (!user.getEmail().equalsIgnoreCase(normalizeEmail(email))) {
            throw new IllegalArgumentException("Username and email do not match.");
        }
        if (!user.isEmailVerified()) {
            throw new IllegalStateException("Please verify your email before requesting a password reset.");
        }
        String token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        user.setResetToken(token);
        userRepository.save(user);
        return token;
    }

    public void resetPassword(String username, String token, String newPassword) {
        User user = requireExistingUser(username);
        if (user.getResetToken() == null || !user.getResetToken().equalsIgnoreCase(token)) {
            throw new IllegalArgumentException("Reset token is invalid.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must contain at least 6 characters.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    public Long resolveSpecialistId(String username) {
        User user = requireExistingUser(username);
        if (user.getRole() != RoleType.SPECIALIST) {
            throw new IllegalArgumentException("Current account is not a specialist.");
        }
        if (user.getSpecialistId() == null) {
            throw new IllegalStateException("Specialist account is not linked to a specialist profile.");
        }
        return specialistService.getSpecialistById(user.getSpecialistId()).getId();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(normalizeUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                .disabled(!user.isEmailVerified())
                .build();
    }

    private void ensureEmailAvailable(String email, String currentUsername) {
        boolean duplicated = currentUsername == null
                ? userRepository.existsByEmailIgnoreCase(email)
                : userRepository.existsByEmailIgnoreCaseAndUsernameNotIgnoreCase(email, normalizeUsername(currentUsername));
        if (duplicated) {
            throw new IllegalArgumentException("Email is already in use.");
        }
    }

    private User requireExistingUser(String username) {
        return userRepository.findByUsernameIgnoreCase(normalizeUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        return username.trim().toLowerCase();
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        return email.trim().toLowerCase();
    }

    private String normalizeRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
    }
}
