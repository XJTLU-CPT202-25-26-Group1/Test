package com.cpt202.booking.controller.customer;

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
@RequestMapping("/customer/profile")
public class CustomerProfileController {

    private final UserService userService;

    public CustomerProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profilePage(Authentication authentication, Model model) {
        model.addAttribute("profile", userService.getByUsername(authentication.getName()));
        return "customer/profile";
    }

    @PostMapping
    public String updateProfile(Authentication authentication,
                                @RequestParam String displayName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(authentication.getName(), displayName, email, phone);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customer/profile";
    }
}
