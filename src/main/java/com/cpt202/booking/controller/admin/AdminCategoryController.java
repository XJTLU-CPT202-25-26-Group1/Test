package com.cpt202.booking.controller.admin;

import com.cpt202.booking.service.ExpertiseCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final ExpertiseCategoryService categoryService;

    public AdminCategoryController(ExpertiseCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String categoriesPage(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories";
    }

    @PostMapping
    public String createCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            categoryService.createCategory(name);
            redirectAttributes.addFlashAttribute("message", "Category created.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/update")
    public String updateCategory(@RequestParam Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategory(id, name);
            redirectAttributes.addFlashAttribute("message", "Category updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/toggle")
    public String toggleCategory(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.toggleCategoryStatus(id);
            redirectAttributes.addFlashAttribute("message", "Category status updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
