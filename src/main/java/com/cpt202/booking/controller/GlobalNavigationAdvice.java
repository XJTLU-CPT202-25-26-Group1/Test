package com.cpt202.booking.controller;

import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.model.User;
import com.cpt202.booking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class GlobalNavigationAdvice {

    private final UserService userService;

    public GlobalNavigationAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void populateNavigation(Authentication authentication, HttpServletRequest request, Model model) {
        boolean authenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("isAuthenticated", authenticated);
        model.addAttribute("isAdmin", false);
        model.addAttribute("isCustomer", false);
        model.addAttribute("isSpecialist", false);

        if (!authenticated) {
            model.addAttribute("currentDisplayName", "Guest");
            model.addAttribute("currentUsername", "guest");
            model.addAttribute("currentRoleLabel", "Guest");
            model.addAttribute("dashboardPath", "/auth/login");
            model.addAttribute("profilePath", "/auth/login");
            return;
        }

        User user = userService.getByUsername(authentication.getName());
        RoleType role = user.getRole();

        model.addAttribute("currentUsername", user.getUsername());
        model.addAttribute("currentDisplayName", user.getDisplayName());
        model.addAttribute("currentRoleLabel", toRoleLabel(role));
        model.addAttribute("dashboardPath", dashboardPath(role));
        model.addAttribute("profilePath", profilePath(role));
        model.addAttribute("isAdmin", role == RoleType.ADMIN);
        model.addAttribute("isCustomer", role == RoleType.CUSTOMER);
        model.addAttribute("isSpecialist", role == RoleType.SPECIALIST);
    }

    private String toRoleLabel(RoleType role) {
        return switch (role) {
            case ADMIN -> "Administrator";
            case SPECIALIST -> "Academic Expert";
            case CUSTOMER -> "Student / Staff";
        };
    }

    private String dashboardPath(RoleType role) {
        return switch (role) {
            case ADMIN -> "/admin/dashboard";
            case SPECIALIST -> "/specialist/dashboard";
            case CUSTOMER -> "/customer/dashboard";
        };
    }

    private String profilePath(RoleType role) {
        return switch (role) {
            case ADMIN -> "/auth/profile-edit";
            case SPECIALIST -> "/specialist/profile";
            case CUSTOMER -> "/customer/profile";
        };
    }
}
