package com.cpt202.booking.controller.admin;

import com.cpt202.booking.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public String bookingReviewPage(Model model) {
        model.addAttribute("pendingBookings", bookingService.getPendingBookings());
        model.addAttribute("allBookings", bookingService.getAllBookings());
        model.addAttribute("auditLogs", bookingService.getAuditLogs());
        return "admin/bookings";
    }

    @GetMapping("/detail")
    public String bookingDetail(@RequestParam Long id, Model model) {
        model.addAttribute("booking", bookingService.getBookingDetail(id));
        model.addAttribute("auditLogs", bookingService.getAuditLogsForBooking(id));
        return "admin/booking-detail";
    }

    @PostMapping("/confirm")
    public String confirm(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.confirmBooking(id);
            redirectAttributes.addFlashAttribute("message", "Appointment request confirmed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        try {
            bookingService.rejectBooking(id, reason);
            redirectAttributes.addFlashAttribute("message", "Appointment request rejected.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}
