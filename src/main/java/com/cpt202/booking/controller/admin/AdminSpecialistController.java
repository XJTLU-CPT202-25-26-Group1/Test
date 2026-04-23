package com.cpt202.booking.controller.admin;

import com.cpt202.booking.enums.SpecialistStatus;
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
    public String specialistsPage(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) SpecialistStatus status,
                                  Model model) {
        model.addAttribute("specialists", specialistService.searchSpecialistsForAdmin(keyword, categoryId, status));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", SpecialistStatus.values());
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status == null ? "" : status.name());
        return "admin/specialists";
    }

    @GetMapping("/edit")
    public String specialistEditPage(@RequestParam Long id, Model model) {
        model.addAttribute("specialist", specialistService.getSpecialistById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/specialist-edit";
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

    @PostMapping("/approve")
    public String approveSpecialist(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            specialistService.approveSpecialist(id);
            redirectAttributes.addFlashAttribute("message", "Specialist approved successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/specialists";
    }

    @PostMapping("/reject")
    public String rejectSpecialist(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            specialistService.rejectSpecialist(id);
            redirectAttributes.addFlashAttribute("message", "Specialist rejected successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/specialists";
    }
}
