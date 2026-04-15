package com.cpt202.booking.controller.customer;

import com.cpt202.booking.service.AvailabilityService;
import com.cpt202.booking.service.SpecialistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/customer/specialists")
public class CustomerSpecialistController {

    private final SpecialistService specialistService;
    private final AvailabilityService availabilityService;

    public CustomerSpecialistController(SpecialistService specialistService, AvailabilityService availabilityService) {
        this.specialistService = specialistService;
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public String specialistList(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("specialists", specialistService.searchSpecialists(keyword));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "customer/specialists";
    }

    @GetMapping("/detail")
    public String specialistDetail(@RequestParam Long id, Model model) {
        model.addAttribute("specialist", specialistService.getAllSpecialists().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found.")));
        model.addAttribute("slots", availabilityService.getAvailableSlots(id));
        return "customer/specialist-detail";
    }
}
