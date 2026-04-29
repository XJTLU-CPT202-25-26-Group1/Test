package com.cpt202.booking.controller;

import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.ExpertiseCategoryService;
import com.cpt202.booking.service.SpecialistService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.Map;

@Controller
public class PageController {

    private static final String DEFAULT_AVATAR_PATH = "/images/avatar-placeholder.svg";

    private final SpecialistService specialistService;
    private final BookingService bookingService;
    private final ExpertiseCategoryService categoryService;
    private final UserService userService;

    public PageController(SpecialistService specialistService,
                          BookingService bookingService,
                          ExpertiseCategoryService categoryService,
                          UserService userService) {
        this.specialistService = specialistService;
        this.bookingService = bookingService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            return resolveDashboard(authentication);
        }
        var activeSpecialists = specialistService.searchSpecialists(null, null, null);
        Map<Long, String> specialistAvatarMap = userService.buildSpecialistAvatarMap(
                activeSpecialists.stream().map(Specialist::getId).toList()
        );
        var featuredSpecialists = activeSpecialists.stream()
                .sorted(Comparator
                        .comparing((Specialist specialist) -> hasUploadedAvatar(specialistAvatarMap.get(specialist.getId())))
                        .reversed()
                        .thenComparing(Specialist::getName, String.CASE_INSENSITIVE_ORDER))
                .limit(4)
                .toList();

        model.addAttribute("featuredSpecialists", featuredSpecialists);
        model.addAttribute("activeCategories", categoryService.getActiveCategories().stream().limit(8).toList());
        model.addAttribute("specialistAvatarMap", specialistAvatarMap);
        model.addAttribute("specialistCount", activeSpecialists.size());
        model.addAttribute("categoryCount", categoryService.getActiveCategories().size());
        model.addAttribute("bookingCount", bookingService.getAllBookings().size());
        model.addAttribute("pendingCount", bookingService.getPendingBookings().size());
        return "home";
    }

    private boolean hasUploadedAvatar(String avatarPath) {
        return avatarPath != null && !avatarPath.isBlank() && !DEFAULT_AVATAR_PATH.equals(avatarPath);
    }

    private String resolveDashboard(Authentication authentication) {
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
        return "home";
    }
}
