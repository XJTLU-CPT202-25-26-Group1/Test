package com.cpt202.booking.controller.admin;

import com.cpt202.booking.enums.BookingStatus;
import com.cpt202.booking.model.Booking;
import com.cpt202.booking.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/billing")
public class AdminBillingController {

    private final BookingService bookingService;

    public AdminBillingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public String billingPage(Model model) {
        List<Booking> billableBookings = bookingService.getAllBookings().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.COMPLETED)
                .toList();

        double totalConfirmedValue = billableBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .mapToDouble(booking -> booking.getCalculatedFee() == null ? 0.0 : booking.getCalculatedFee())
                .sum();

        double totalCompletedRevenue = billableBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(booking -> booking.getCalculatedFee() == null ? 0.0 : booking.getCalculatedFee())
                .sum();

        model.addAttribute("billableBookings", billableBookings);
        model.addAttribute("confirmedValue", totalConfirmedValue);
        model.addAttribute("completedRevenue", totalCompletedRevenue);
        model.addAttribute("billableCount", billableBookings.size());
        return "admin/billing";
    }
}
