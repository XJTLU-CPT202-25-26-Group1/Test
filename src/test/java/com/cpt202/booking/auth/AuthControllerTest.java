package com.cpt202.booking.auth;

import com.cpt202.booking.controller.auth.AuthController;
import com.cpt202.booking.enums.RoleType;
import com.cpt202.booking.model.User;
import com.cpt202.booking.service.EmailService;
import com.cpt202.booking.service.ExpertiseCategoryService;
import com.cpt202.booking.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    @Test
    void registrationReportsSuccessEvenWhenEmailSendingFails() {
        User created = new User("newuser", "encoded", "New User", "newuser@example.com", "13800009999", RoleType.CUSTOMER);
        UserService userService = new UserService(null, null, null) {
            @Override
            public User registerUser(String username,
                                     String password,
                                     String displayName,
                                     String email,
                                     String phone,
                                     RoleType role,
                                     Long categoryId,
                                     String level,
                                     Double feeRate,
                                     String description) {
                return created;
            }
        };
        ExpertiseCategoryService categoryService = new ExpertiseCategoryService(null);
        EmailService emailService = new EmailService(null, false, "", "") {
            @Override
            public void sendVerificationEmail(User user) {
                throw new IllegalStateException("Mail unavailable");
            }
        };

        AuthController controller = new AuthController(userService, categoryService, emailService);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.registerUser(
                "newuser",
                "password123",
                "New User",
                "newuser@example.com",
                "13800009999",
                RoleType.CUSTOMER,
                null,
                null,
                null,
                null,
                redirectAttributes
        );

        assertEquals("redirect:/auth/login", view);
        assertEquals(
                "Registration successful, but the verification email could not be sent. Please use resend verification.",
                redirectAttributes.getFlashAttributes().get("message")
        );
    }
}
