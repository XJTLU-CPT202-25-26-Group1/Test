package com.cpt202.booking.controller.customer;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.model.User;
import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerDashboardController {

    private final BookingService bookingService;
    private final UserService userService;

    public CustomerDashboardController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.getByUsername(authentication.getName());
        List<Booking> bookings = bookingService.getCustomerBookings(user.getEmail());
        List<Booking> upcomingBookings = bookingService.getUpcomingBookings(user.getEmail());
        model.addAttribute("profile", user);
        model.addAttribute("bookingCount", bookings.size());
        model.addAttribute("pendingCount", bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.PENDING).count());
        model.addAttribute("completedCount", bookings.stream().filter(booking -> booking.getStatus() == BookingStatus.COMPLETED).count());
        model.addAttribute("upcomingBookings", upcomingBookings);
        model.addAttribute("specialistAvatarMap",
                userService.buildSpecialistAvatarMap(
                        upcomingBookings.stream()
                                .map(booking -> booking.getSpecialist().getId())
                                .toList()));
        model.addAttribute("recentBookings", bookings.stream().limit(5).toList());
        model.addAttribute("notifications", bookingService.getCustomerNotifications(user.getEmail()));
        return "customer/dashboard";
    }
}
