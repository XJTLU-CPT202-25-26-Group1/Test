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
    public String specialistsPage(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("specialists", specialistService.searchSpecialists(keyword));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
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

    @PostMapping("/update")
    public String updateSpecialist(@RequestParam Long id,
                                   @RequestParam String name,
                                   @RequestParam String level,
                                   @RequestParam Double feeRate,
                                   @RequestParam String description,
                                   @RequestParam Long categoryId,
                                   RedirectAttributes redirectAttributes) {
        try {
            specialistService.updateSpecialist(id, name, level, feeRate, description, categoryId);
            redirectAttributes.addFlashAttribute("message", "Specialist updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/specialists";
    }

    @PostMapping("/toggle")
    public String toggleSpecialist(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            specialistService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("message", "Specialist status updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/specialists";
    }
}
