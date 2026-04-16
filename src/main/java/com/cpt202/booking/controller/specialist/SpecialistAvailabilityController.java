package com.cpt202.booking.controller.specialist;

import com.cpt202.booking.service.AvailabilityService;
import com.cpt202.booking.service.SpecialistService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/specialist/availability")
public class SpecialistAvailabilityController {

    private final AvailabilityService availabilityService;
    private final SpecialistService specialistService;
    private final UserService userService;

    public SpecialistAvailabilityController(AvailabilityService availabilityService,
                                            SpecialistService specialistService,
                                            UserService userService) {
        this.availabilityService = availabilityService;
        this.specialistService = specialistService;
        this.userService = userService;
    }

    @GetMapping
    public String availabilityPage(Authentication authentication, Model model) {
        Long specialistId = userService.resolveSpecialistId(authentication.getName());
        model.addAttribute("specialist", specialistService.getSpecialistById(specialistId));
        model.addAttribute("slots", availabilityService.getSlotsForSpecialist(specialistId));
        return "specialist/availability";
    }

    @PostMapping
    public String createSlot(Authentication authentication,
                             @RequestParam String date,
                             @RequestParam String startTime,
                             @RequestParam String endTime,
                             RedirectAttributes redirectAttributes) {
        try {
            Long specialistId = userService.resolveSpecialistId(authentication.getName());
            availabilityService.createSlot(specialistId, LocalDate.parse(date), LocalTime.parse(startTime), LocalTime.parse(endTime));
            redirectAttributes.addFlashAttribute("message", "Availability slot added.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/specialist/availability";
    }

    @PostMapping("/update")
    public String updateSlot(Authentication authentication,
                             @RequestParam Long slotId,
                             @RequestParam String date,
                             @RequestParam String startTime,
                             @RequestParam String endTime,
                             RedirectAttributes redirectAttributes) {
        try {
            Long specialistId = userService.resolveSpecialistId(authentication.getName());
            availabilityService.updateSlot(specialistId, slotId, LocalDate.parse(date), LocalTime.parse(startTime), LocalTime.parse(endTime));
            redirectAttributes.addFlashAttribute("message", "Availability slot updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/specialist/availability";
    }

    @PostMapping("/delete")
    public String deleteSlot(Authentication authentication,
                             @RequestParam Long slotId,
                             RedirectAttributes redirectAttributes) {
        try {
            Long specialistId = userService.resolveSpecialistId(authentication.getName());
            availabilityService.deleteSlot(specialistId, slotId);
            redirectAttributes.addFlashAttribute("message", "Availability slot deleted.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/specialist/availability";
    }
}
