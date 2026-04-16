package com.cpt202.booking.service;

import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.model.ExpertiseCategory;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService implements UserDetailsService {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final SpecialistService specialistService;
    private final ExpertiseCategoryService categoryService;

    public UserService(SpecialistService specialistService, ExpertiseCategoryService categoryService) {
        this.specialistService = specialistService;
        this.categoryService = categoryService;

        users.put("admin", new User("admin", "admin123", "System Admin", "admin@demo.local", "13800000001", RoleType.ADMIN));
        users.put("specialist", new User("specialist", "specialist123", "Demo Specialist", "specialist@demo.local", "13800000002", RoleType.SPECIALIST));
        users.put("customer", new User("customer", "customer123", "Demo Customer", "customer@demo.local", "13800000003", RoleType.CUSTOMER));
    }

    public User getByUsername(String username) {
        return users.computeIfAbsent(username,
                key -> new User(key, key + "123", key, key + "@demo.local", "13800000000", RoleType.CUSTOMER));
    }

    public User updateProfile(String username, String displayName, String email, String phone) {
        ensureEmailAvailable(email, username);
        User user = requireExistingUser(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setPhone(phone);
        users.put(username, user);
        return user;
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
        if (role == RoleType.ADMIN) {
            throw new IllegalArgumentException("Administrator accounts are pre-provisioned only.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must contain at least 6 characters.");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name is required.");
        }
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username is already in use.");
        }
        ensureEmailAvailable(email, null);

        User user = new User(username, password, displayName, email, phone, role);
        if (role == RoleType.SPECIALIST) {
            if (categoryId == null) {
                throw new IllegalArgumentException("Specialist registration requires an expertise category.");
            }
            Double finalFeeRate = feeRate == null ? 200.0 : feeRate;
            String finalLevel = (level == null || level.isBlank()) ? "Associate" : level;
            String finalDescription = (description == null || description.isBlank())
                    ? "Self-registered specialist profile pending enrichment."
                    : description;
            Specialist specialist = specialistService.createSpecialist(displayName, finalLevel, finalFeeRate, finalDescription, categoryId);
            user.setSpecialistId(specialist.getId());
        }
        users.put(username, user);
        return user;
    }

    public String createResetToken(String username, String email) {
        User user = requireExistingUser(username);
        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException("Username and email do not match.");
        }
        String token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        user.setResetToken(token);
        users.put(username, user);
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
        user.setPassword(newPassword);
        user.setResetToken(null);
        users.put(username, user);
    }

    public Long resolveSpecialistId(String username) {
        User user = requireExistingUser(username);
        if (user.getRole() != RoleType.SPECIALIST) {
            throw new IllegalArgumentException("Current account is not a specialist.");
        }
        if (user.getSpecialistId() != null) {
            return user.getSpecialistId();
        }
        List<Specialist> specialists = specialistService.getAllSpecialists();
        if (specialists.isEmpty()) {
            List<ExpertiseCategory> categories = categoryService.getActiveCategories();
            Long categoryId = categories.isEmpty() ? null : categories.get(0).getId();
            if (categoryId == null) {
                throw new IllegalStateException("No expertise category is available for specialist initialization.");
            }
            Specialist created = specialistService.createSpecialist(
                    user.getDisplayName(),
                    "Associate",
                    200.0,
                    "Auto-created profile for specialist account.",
                    categoryId
            );
            user.setSpecialistId(created.getId());
            return created.getId();
        }
        user.setSpecialistId(specialists.get(0).getId());
        return user.getSpecialistId();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                .build();
    }

    private void ensureEmailAvailable(String email, String currentUsername) {
        boolean duplicated = users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)
                        && (currentUsername == null || !user.getUsername().equalsIgnoreCase(currentUsername)));
        if (duplicated) {
            throw new IllegalArgumentException("Email is already in use.");
        }
    }

    private User requireExistingUser(String username) {
        User user = users.get(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user;
    }
}
