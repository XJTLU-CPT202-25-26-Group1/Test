package com.cpt202.booking.controller.customer;

import com.cpt202.booking.service.AvailabilityService;
import com.cpt202.booking.service.ExpertiseCategoryService;
import com.cpt202.booking.service.SpecialistService;
import com.cpt202.booking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/customer/specialists")
public class CustomerSpecialistController {

    private final SpecialistService specialistService;
    private final AvailabilityService availabilityService;
    private final ExpertiseCategoryService categoryService;
    private final UserService userService;

    public CustomerSpecialistController(SpecialistService specialistService,
                                       AvailabilityService availabilityService,
                                       ExpertiseCategoryService categoryService,
                                       UserService userService) {
        this.specialistService = specialistService;
        this.availabilityService = availabilityService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping
    public String specialistList(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Long categoryId,
                                 @RequestParam(required = false) String availableDate,
                                 Model model) {
        LocalDate parsedDate = (availableDate == null || availableDate.isBlank()) ? null : LocalDate.parse(availableDate);
        var specialists = specialistService.searchSpecialists(keyword, categoryId, parsedDate);
        model.addAttribute("specialists", specialists);
        model.addAttribute("specialistAvatarMap",
                userService.buildSpecialistAvatarMap(specialists.stream().map(specialist -> specialist.getId()).toList()));
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("availableDate", availableDate == null ? "" : availableDate);
        return "customer/specialists";
    }

    @GetMapping("/detail")
    public String specialistDetail(@RequestParam Long id,
                                   @RequestParam(required = false) String availableDate,
                                   Model model) {
        LocalDate parsedDate = (availableDate == null || availableDate.isBlank()) ? null : LocalDate.parse(availableDate);
        model.addAttribute("specialist", specialistService.getSpecialistById(id));
        model.addAttribute("specialistAvatarPath", userService.getSpecialistAvatarPath(id));
        model.addAttribute("slots", availabilityService.getAvailableSlots(id, parsedDate));
        model.addAttribute("availableDate", availableDate == null ? "" : availableDate);
        return "customer/specialist-detail";
    }
}
