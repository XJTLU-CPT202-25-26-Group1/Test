package com.cpt202.booking.controller.customer;

import com.cpt202.booking.service.BookingService;
import com.cpt202.booking.service.FeedbackService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/bookings")
public class CustomerBookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final FeedbackService feedbackService;

    public CustomerBookingController(BookingService bookingService,
                                     UserService userService,
                                     FeedbackService feedbackService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public String bookingList(Authentication authentication,
                              @RequestParam(required = false) String keyword,
                              Model model) {
        String email = userService.getByUsername(authentication.getName()).getEmail();
        model.addAttribute("bookings", bookingService.searchCustomerBookings(email, keyword));
        model.addAttribute("upcomingBookings", bookingService.getUpcomingBookings(email));
        model.addAttribute("notifications", bookingService.getCustomerNotifications(email));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "customer/bookings";
    }

    @GetMapping("/detail")
    public String bookingDetail(Authentication authentication, @RequestParam Long id, Model model) {
        String email = userService.getByUsername(authentication.getName()).getEmail();
        model.addAttribute("booking", bookingService.getCustomerBookingDetail(id, email));
        model.addAttribute("existingFeedback", feedbackService.getFeedbackForBooking(id).orElse(null));
        return "customer/booking-detail";
    }

    @PostMapping("/create")
    public String createBooking(Authentication authentication,
                                @RequestParam Long specialistId,
                                @RequestParam Long slotId,
                                @RequestParam String topic,
                                @RequestParam(required = false) String notes,
                                RedirectAttributes redirectAttributes) {
        try {
            String email = userService.getByUsername(authentication.getName()).getEmail();
            String displayName = userService.getByUsername(authentication.getName()).getDisplayName();
            bookingService.createBooking(displayName, email, specialistId, slotId, topic, notes);
            redirectAttributes.addFlashAttribute("message", "Booking request submitted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customer/bookings";
    }

    @PostMapping("/cancel")
    public String cancelBooking(Authentication authentication,
                               @RequestParam Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            String email = userService.getByUsername(authentication.getName()).getEmail();
            bookingService.cancelBookingForCustomer(id, email);
            redirectAttributes.addFlashAttribute("message", "Booking cancelled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customer/bookings";
    }

    @PostMapping("/reschedule")
    public String rescheduleBooking(Authentication authentication,
                                    @RequestParam Long id,
                                    @RequestParam Long newSlotId,
                                    RedirectAttributes redirectAttributes) {
        try {
            String email = userService.getByUsername(authentication.getName()).getEmail();
            bookingService.rescheduleBookingForCustomer(id, newSlotId, email);
            redirectAttributes.addFlashAttribute("message", "Booking rescheduled and returned to pending review.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customer/bookings";
    }

    @PostMapping("/feedback")
    public String submitFeedback(Authentication authentication,
                                 @RequestParam Long bookingId,
                                 @RequestParam Integer rating,
                                 @RequestParam String comment,
                                 RedirectAttributes redirectAttributes) {
        try {
            String email = userService.getByUsername(authentication.getName()).getEmail();
            feedbackService.submitFeedback(bookingId, email, rating, comment);
            redirectAttributes.addFlashAttribute("message", "Feedback submitted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customer/bookings/detail?id=" + bookingId;
    }
}
