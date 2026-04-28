package com.cpt202.booking.service;

import com.cpt202.booking.enums.GenderType;
import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.enums.SpecialistStatus;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.model.User;
import com.cpt202.booking.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static final String DEFAULT_AVATAR_PATH = "/images/avatar-placeholder.svg";

    private final UserRepository userRepository;
    private final SpecialistService specialistService;
    private final PasswordEncoder passwordEncoder;
    private final AvatarStorageService avatarStorageService;

    public UserService(UserRepository userRepository,
                       SpecialistService specialistService,
                       PasswordEncoder passwordEncoder,
                       AvatarStorageService avatarStorageService) {
        this.userRepository = userRepository;
        this.specialistService = specialistService;
        this.passwordEncoder = passwordEncoder;
        this.avatarStorageService = avatarStorageService;
    }

    public User getByUsername(String username) {
        return requireExistingUser(username);
    }

    public User updateProfile(String username, String displayName, String email, String phone) {
        return updateProfile(username, displayName, email, phone, null, null);
    }

    @Transactional
    public User updateAvatar(String username, MultipartFile avatar) {
        User user = requireExistingUser(username);
        if (!hasUploadedFile(avatar)) {
            throw new IllegalArgumentException("Please choose an avatar image before submitting.");
        }

        String previousAvatarPath = user.getAvatarPath();
        String nextAvatarPath = avatarStorageService.storeAvatar(avatar, user.getUsername());
        user.setAvatarPath(nextAvatarPath);

        try {
            User savedUser = userRepository.save(user);
            if (previousAvatarPath != null && !previousAvatarPath.equals(nextAvatarPath)) {
                avatarStorageService.deleteAvatar(previousAvatarPath);
            }
            return savedUser;
        } catch (RuntimeException ex) {
            avatarStorageService.deleteAvatar(nextAvatarPath);
            throw ex;
        }
    }

    @Transactional
    public User updateProfile(String username,
                              String displayName,
                              String email,
                              String phone,
                              GenderType gender,
                              MultipartFile avatar) {
        String normalizedEmail = normalizeEmail(email);
        ensureEmailAvailable(normalizedEmail, username);
        User user = requireExistingUser(username);
        String previousAvatarPath = user.getAvatarPath();
        String nextAvatarPath = previousAvatarPath;

        if (hasUploadedFile(avatar)) {
            nextAvatarPath = avatarStorageService.storeAvatar(avatar, user.getUsername());
        }

        user.setDisplayName(normalizeRequiredText(displayName, "Display name"));
        user.setEmail(normalizedEmail);
        user.setPhone(normalizeRequiredText(phone, "Phone"));
        if (gender != null) {
            user.setGender(normalizeGender(gender));
        }
        user.setAvatarPath(nextAvatarPath);

        try {
            User savedUser = userRepository.save(user);
            if (hasUploadedFile(avatar) && previousAvatarPath != null && !previousAvatarPath.equals(nextAvatarPath)) {
                avatarStorageService.deleteAvatar(previousAvatarPath);
            }
            return savedUser;
        } catch (RuntimeException ex) {
            if (hasUploadedFile(avatar) && nextAvatarPath != null && !nextAvatarPath.equals(previousAvatarPath)) {
                avatarStorageService.deleteAvatar(nextAvatarPath);
            }
            throw ex;
        }
    }

    @Transactional
    public User registerUser(String username,
                             String password,
                             String displayName,
                             String email,
                             String phone,
                             GenderType gender,
                             RoleType role,
                             Long categoryId,
                             String level,
                             Double feeRate,
                             String description,
                             MultipartFile avatar) {
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
                normalizeGender(gender),
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
            Specialist specialist = specialistService.createPendingSpecialist(displayName, finalLevel, finalFeeRate, finalDescription, categoryId);
            user.setSpecialistId(specialist.getId());
        }

        String avatarPath = hasUploadedFile(avatar) ? avatarStorageService.storeAvatar(avatar, normalizedUsername) : null;
        user.setAvatarPath(avatarPath);
        try {
            return userRepository.save(user);
        } catch (RuntimeException ex) {
            if (avatarPath != null) {
                avatarStorageService.deleteAvatar(avatarPath);
            }
            throw ex;
        }
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

    public Map<Long, String> buildSpecialistAvatarMap(List<Long> specialistIds) {
        if (specialistIds == null || specialistIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, String> avatarPathsBySpecialistId = userRepository.findAllBySpecialistIdIn(specialistIds).stream()
                .filter(user -> user.getSpecialistId() != null)
                .collect(Collectors.toMap(User::getSpecialistId, this::resolveAvatarPath, (left, right) -> left));

        return specialistIds.stream()
                .distinct()
                .collect(Collectors.toMap(Function.identity(),
                        id -> avatarPathsBySpecialistId.getOrDefault(id, DEFAULT_AVATAR_PATH)));
    }

    public String getSpecialistAvatarPath(Long specialistId) {
        return userRepository.findBySpecialistId(specialistId)
                .map(this::resolveAvatarPath)
                .orElse(DEFAULT_AVATAR_PATH);
    }

    public String getUserAvatarPath(User user) {
        return resolveAvatarPath(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(normalizeUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                .disabled(!canUserLogin(user))
                .build();
    }

    public String resolveLoginBlockReason(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username == null ? "" : username.trim().toLowerCase())
                .orElse(null);
        if (user == null) {
            return null;
        }
        if (!user.isEmailVerified()) {
            return "unverified";
        }
        if (user.getRole() != RoleType.SPECIALIST) {
            return null;
        }
        if (user.getSpecialistId() == null) {
            return "specialist_pending";
        }

        Specialist specialist;
        try {
            specialist = specialistService.getSpecialistById(user.getSpecialistId());
        } catch (IllegalArgumentException ex) {
            return "specialist_pending";
        }

        return switch (specialist.getStatus()) {
            case PENDING_APPROVAL -> "pendingApproval";
            case REJECTED -> "approvalRejected";
            case INACTIVE -> "specialistInactive";
            case ACTIVE -> null;
        };
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

    private GenderType normalizeGender(GenderType gender) {
        if (gender == null || gender == GenderType.UNSPECIFIED) {
            throw new IllegalArgumentException("Gender must be either Male or Female.");
        }
        return gender;
    }

    private String resolveAvatarPath(User user) {
        if (user == null || user.getAvatarPath() == null || user.getAvatarPath().isBlank()) {
            return DEFAULT_AVATAR_PATH;
        }
        return user.getAvatarPath();
    }

    private boolean hasUploadedFile(MultipartFile avatar) {
        return avatar != null && !avatar.isEmpty();
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
    }

    private boolean canUserLogin(User user) {
        if (!user.isEmailVerified()) {
            return false;
        }
        if (user.getRole() != RoleType.SPECIALIST) {
            return true;
        }
        if (user.getSpecialistId() == null) {
            return false;
        }
        try {
            return specialistService.getSpecialistById(user.getSpecialistId()).getStatus() == SpecialistStatus.ACTIVE;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
