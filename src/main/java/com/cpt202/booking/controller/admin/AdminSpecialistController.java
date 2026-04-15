package com.cpt202.booking.controller.admin;

import com.cpt202.booking.service.ExpertiseCategoryService;
import com.cpt202.booking.service.SpecialistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/specialists")
public class AdminSpecialistController {

    private final SpecialistService specialistService;
    private final ExpertiseCategoryService categoryService;

    public AdminSpecialistController(SpecialistService specialistService, ExpertiseCategoryService categoryService) {
        this.specialistService = specialistService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String specialistsPage(Model model) {
        model.addAttribute("specialists", specialistService.getAllSpecialists());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/specialists";
    }

    @PostMapping
    public String createSpecialist(@RequestParam String name,
                                   @RequestParam String level,
                                   @RequestParam Double feeRate,
                                   @RequestParam String description,
                                   @RequestParam Long categoryId,
                                   RedirectAttributes redirectAttributes) {
        try {
            specialistService.createSpecialist(name, level, feeRate, description, categoryId);
            redirectAttributes.addFlashAttribute("message", "Specialist created.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/specialists";
    }
}
