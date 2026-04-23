package com.cpt202.booking.controller.auth;

import com.cpt202.booking.enums.GenderType;
import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.model.User;
import com.cpt202.booking.service.EmailService;
import com.cpt202.booking.service.ExpertiseCategoryService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final ExpertiseCategoryService categoryService;
    private final EmailService emailService;

    public AuthController(UserService userService,
                          ExpertiseCategoryService categoryService,
                          EmailService emailService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.emailService = emailService;
    }

    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String register(Model model) {
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("genders", GenderType.values());
        model.addAttribute("roles", new RoleType[]{RoleType.CUSTOMER, RoleType.SPECIALIST});
        return "auth/register";
    }

    @GetMapping("/auth/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @GetMapping("/auth/resend-verification")
    public String resendVerification() {
        return "auth/resend-verification";
    }

    @GetMapping("/auth/reset-password")
    public String resetPassword(@RequestParam(required = false) String username,
                                @RequestParam(required = false) String token,
                                Model model) {
        model.addAttribute("username", username == null ? "" : username);
        model.addAttribute("token", token == null ? "" : token);
        return "auth/reset-password";
    }

    @PostMapping("/auth/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String displayName,
                               @RequestParam String email,
                               @RequestParam String phone,
                               @RequestParam GenderType gender,
                               @RequestParam RoleType role,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) String level,
                               @RequestParam(required = false) Double feeRate,
                               @RequestParam(required = false) String description,
                               RedirectAttributes redirectAttributes) {
        User user;
        try {
            user = userService.registerUser(username, password, displayName, email, phone, gender, role, categoryId, level, feeRate, description);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/auth/register";
        }

        try {
            emailService.sendVerificationEmail(user);
            redirectAttributes.addFlashAttribute("message",
                    role == RoleType.SPECIALIST
                            ? "Specialist registration submitted. Please verify your email and wait for administrator approval. You will be notified by email after review."
                            : "Registration successful. Please verify your email before logging in.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message",
                    role == RoleType.SPECIALIST
                            ? "Specialist registration submitted, but the verification email could not be sent. Please use resend verification and wait for administrator approval."
                            : "Registration successful, but the verification email could not be sent. Please use resend verification.");
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/verify-email")
    public String verifyEmail(@RequestParam String username,
                              @RequestParam String token,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.verifyEmail(username, token);
            redirectAttributes.addFlashAttribute("message", "Email verified successfully. You can now log in.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/auth/login";
    }

    @PostMapping("/auth/forgot-password")
    public String sendResetToken(@RequestParam String username,
                                 @RequestParam String email,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.createResetToken(username, email);
            emailService.sendResetPasswordEmail(userService.getByUsername(username));
            redirectAttributes.addFlashAttribute("message",
                    "Password reset instructions have been sent to your email.");
            return "redirect:/auth/login";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/auth/forgot-password";
        }
    }

    @PostMapping("/auth/resend-verification")
    public String resendVerification(@RequestParam String username,
                                     @RequestParam String email,
                                     RedirectAttributes redirectAttributes) {
        try {
            User user = userService.resendVerificationToken(username, email);
            emailService.sendVerificationEmail(user);
            redirectAttributes.addFlashAttribute("message", "A new verification email has been sent.");
            return "redirect:/auth/login";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/auth/resend-verification";
        }
    }

    @PostMapping("/auth/reset-password")
    public String resetPassword(@RequestParam String username,
                                @RequestParam String token,
                                @RequestParam String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.resetPassword(username, token, newPassword);
            redirectAttributes.addFlashAttribute("message", "Password reset completed. Please log in again.");
            return "redirect:/auth/login";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/auth/reset-password?username=" + username + "&token=" + token;
        }
    }

    @GetMapping("/auth/profile-edit")
    public String profileEdit(Authentication authentication, Model model) {
        if (authentication != null) {
                model.addAttribute("profile", userService.getByUsername(authentication.getName()));
        }
        return "auth/profile-edit";
    }

    @PostMapping("/auth/profile-edit")
    public String updateProfile(Authentication authentication,
                                @RequestParam String displayName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                RedirectAttributes redirectAttributes) {
        if (authentication != null) {
            try {
                userService.updateProfile(authentication.getName(), displayName, email, phone);
                redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("message", ex.getMessage());
            }
        }
        return "redirect:/auth/profile-edit";
    }

    @GetMapping("/auth/success")
    public String success(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/";
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return "redirect:/admin/dashboard";
            }
            if ("ROLE_SPECIALIST".equals(authority.getAuthority())) {
                return "redirect:/specialist/dashboard";
            }
            if ("ROLE_CUSTOMER".equals(authority.getAuthority())) {
                return "redirect:/customer/dashboard";
            }
        }
        return "redirect:/";
    }
}
