package com.cpt202.booking.controller;

import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.SpecialistService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final SpecialistService specialistService;
    private final BookingService bookingService;

    public PageController(SpecialistService specialistService, BookingService bookingService) {
        this.specialistService = specialistService;
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            return resolveDashboard(authentication);
        }
        model.addAttribute("specialistCount", specialistService.getAllSpecialists().size());
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
