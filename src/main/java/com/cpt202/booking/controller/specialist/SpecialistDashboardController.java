package com.cpt202.booking.controller.specialist;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.AvailabilitySlot;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.service.AvailabilityService;
import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.SpecialistService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/specialist")
public class SpecialistDashboardController {

    private final BookingService bookingService;
    private final AvailabilityService availabilityService;
    private final SpecialistService specialistService;
    private final UserService userService;

    public SpecialistDashboardController(BookingService bookingService,
                                         AvailabilityService availabilityService,
                                         SpecialistService specialistService,
                                         UserService userService) {
        this.bookingService = bookingService;
        this.availabilityService = availabilityService;
        this.specialistService = specialistService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Long specialistId = userService.resolveSpecialistId(authentication.getName());
        Specialist specialist = specialistService.getSpecialistById(specialistId);
        List<Booking> bookings = bookingService.getSpecialistBookings(specialistId);
        List<AvailabilitySlot> slots = availabilityService.getSlotsForSpecialist(specialistId);

        model.addAttribute("specialist", specialist);
        model.addAttribute("bookingCount", bookings.size());
        model.addAttribute("confirmedCount", bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED).count());
        model.addAttribute("completedCount", bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.COMPLETED).count());
        model.addAttribute("availableSlotCount", slots.stream().filter(slot -> !slot.isBooked() && !slot.getSlotDate().isBefore(LocalDate.now())).count());
        model.addAttribute("recentBookings", bookings.stream().limit(3).toList());
        model.addAttribute("notifications", bookingService.getSpecialistNotifications(specialistId));
        return "specialist/dashboard";
    }
}
