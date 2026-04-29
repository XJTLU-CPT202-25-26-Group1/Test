package com.cpt202.booking.controller.specialist;

import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Controller
@RequestMapping("/specialist/bookings")
public class SpecialistBookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public SpecialistBookingController(BookingService bookingService,
                                       UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping
    public String bookingPage(Authentication authentication, Model model) {
        Long specialistId = userService.resolveSpecialistId(authentication.getName());
        model.addAttribute("bookings", bookingService.getSpecialistBookings(specialistId));
        model.addAttribute("notifications", bookingService.getSpecialistNotifications(specialistId));
        return "specialist/bookings";
    }

    @GetMapping("/weekly")
    public String weeklySchedule(Authentication authentication,
                                 @RequestParam(required = false) String weekStart,
                                 Model model) {
        Long specialistId = userService.resolveSpecialistId(authentication.getName());
        LocalDate resolvedWeekStart = (weekStart == null || weekStart.isBlank())
                ? LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : LocalDate.parse(weekStart);
        model.addAttribute("weekStart", resolvedWeekStart);
        model.addAttribute("weekEnd", resolvedWeekStart.plusDays(6));
        model.addAttribute("bookings", bookingService.getSpecialistBookingsForWeek(specialistId, resolvedWeekStart));
        model.addAttribute("historyBookings", bookingService.getSpecialistCompletedBookings(specialistId).stream().limit(8).toList());
        return "specialist/booking-history";
    }

    @GetMapping("/detail")
    public String bookingDetail(Authentication authentication,
                                @RequestParam Long id,
                                Model model) {
        Long specialistId = userService.resolveSpecialistId(authentication.getName());
        model.addAttribute("booking", bookingService.getSpecialistBookingDetail(id, specialistId));
        model.addAttribute("auditLogs", bookingService.getAuditLogsForBooking(id));
        return "specialist/booking-detail";
    }

    @PostMapping("/complete")
    public String complete(Authentication authentication,
                           @RequestParam Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            Long specialistId = userService.resolveSpecialistId(authentication.getName());
            bookingService.completeBookingForSpecialist(id, specialistId);
            redirectAttributes.addFlashAttribute("message", "Consultation marked as completed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/specialist/bookings";
    }
}
