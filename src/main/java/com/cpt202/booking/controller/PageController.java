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

@Controller
public class PageController {

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
        var featuredSpecialists = specialistService.getAllSpecialists().stream().limit(6).toList();
        model.addAttribute("featuredSpecialists", featuredSpecialists);
        model.addAttribute("activeCategories", categoryService.getActiveCategories().stream().limit(8).toList());
        model.addAttribute("specialistAvatarMap", userService.buildSpecialistAvatarMap(
                featuredSpecialists.stream().map(Specialist::getId).toList()
        ));
        model.addAttribute("specialistCount", specialistService.getAllSpecialists().size());
        model.addAttribute("categoryCount", categoryService.getActiveCategories().size());
        model.addAttribute("bookingCount", bookingService.getAllBookings().size());
        model.addAttribute("pendingCount", bookingService.getPendingBookings().size());
        return "home";
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
