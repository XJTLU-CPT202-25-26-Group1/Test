package com.cpt202.booking.controller.specialist;

import com.cpt202.booking.service.SpecialistService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/specialist/profile")
public class SpecialistProfileController {

    private final UserService userService;
    private final SpecialistService specialistService;

    public SpecialistProfileController(UserService userService, SpecialistService specialistService) {
        this.userService = userService;
        this.specialistService = specialistService;
    }

    @GetMapping
    public String profilePage(Authentication authentication, Model model) {
        Long specialistId = userService.resolveSpecialistId(authentication.getName());
        model.addAttribute("account", userService.getByUsername(authentication.getName()));
        model.addAttribute("specialist", specialistService.getSpecialistById(specialistId));
        return "specialist/profile";
    }
}
