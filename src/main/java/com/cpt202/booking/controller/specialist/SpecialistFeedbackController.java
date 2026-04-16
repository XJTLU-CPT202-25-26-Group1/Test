package com.cpt202.booking.controller.specialist;

import com.cpt202.booking.service.FeedbackService;
import com.cpt202.booking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/specialist/feedback")
public class SpecialistFeedbackController {

    private final FeedbackService feedbackService;
    private final UserService userService;

    public SpecialistFeedbackController(FeedbackService feedbackService, UserService userService) {
        this.feedbackService = feedbackService;
        this.userService = userService;
    }

    @GetMapping
    public String feedbackPage(Authentication authentication, Model model) {
        Long specialistId = userService.resolveSpecialistId(authentication.getName());
        model.addAttribute("feedbackList", feedbackService.getFeedbackForSpecialist(specialistId));
        return "specialist/feedback";
    }
}
