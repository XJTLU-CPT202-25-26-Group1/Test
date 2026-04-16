package com.cpt202.booking.controller.admin;

import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.ExpertiseCategoryService;
import com.cpt202.booking.service.SpecialistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final BookingService bookingService;
    private final SpecialistService specialistService;
    private final ExpertiseCategoryService categoryService;

    public AdminDashboardController(BookingService bookingService, SpecialistService specialistService, ExpertiseCategoryService categoryService) {
        this.bookingService = bookingService;
        this.specialistService = specialistService;
        this.categoryService = categoryService;
    }

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("pendingCount", bookingService.getPendingBookings().size());
        model.addAttribute("bookingCount", bookingService.getAllBookings().size());
        model.addAttribute("specialistCount", specialistService.getAllSpecialists().size());
        model.addAttribute("categoryCount", categoryService.getAllCategories().size());
        model.addAttribute("recentAuditLogs", bookingService.getAuditLogs().stream().limit(6).toList());
        return "admin/dashboard";
    }
}
