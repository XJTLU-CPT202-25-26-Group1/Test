package com.cpt202.booking.service;

import com.cpt202.booking.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean mailEnabled;
    private final String fromAddress;
    private final String baseUrl;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${app.mail.enabled:false}") boolean mailEnabled,
                        @Value("${app.mail.from:no-reply@example.com}") String fromAddress,
                        @Value("${app.base-url:https://xsbooking.com}") String baseUrl) {
        this.mailSenderProvider = mailSenderProvider;
        this.mailEnabled = mailEnabled;
        this.fromAddress = fromAddress;
        this.baseUrl = baseUrl;
    }

    public void sendVerificationEmail(User user) {
        String verifyLink = baseUrl + "/auth/verify-email?username=" + user.getUsername() + "&token=" + user.getVerificationToken();
        sendEmail(
                user.getEmail(),
                "Verify your Expert Appointment account",
                "Hello " + user.getDisplayName() + ",\n\n"
                        + "Please verify your account email by opening the link below:\n"
                        + verifyLink + "\n\n"
                        + "If you did not create this account, you can ignore this message."
        );
    }

    public void sendResetPasswordEmail(User user) {
        String resetLink = baseUrl + "/auth/reset-password?username=" + user.getUsername() + "&token=" + user.getResetToken();
        sendEmail(
                user.getEmail(),
                "Reset your Expert Appointment password",
                "Hello " + user.getDisplayName() + ",\n\n"
                        + "Use the link below to reset your password:\n"
                        + resetLink + "\n\n"
                        + "Reset token: " + user.getResetToken() + "\n\n"
                        + "If you did not request a password reset, you can ignore this message."
        );
    }

    public void sendSpecialistApprovalEmail(User user, boolean emailVerified) {
        sendEmail(
                user.getEmail(),
                "Your specialist registration has been approved",
                "Hello " + user.getDisplayName() + ",\n\n"
                        + "Your specialist registration has been approved by the administrator.\n"
                        + (emailVerified
                        ? "You can now log in to the system and start using your specialist portal."
                        : "Please verify your email first. After email verification, you will be able to log in.")
                        + "\n\n"
                        + "Login page: " + baseUrl + "/auth/login"
        );
    }

    public void sendSpecialistRejectionEmail(User user) {
        sendEmail(
                user.getEmail(),
                "Your specialist registration was not approved",
                "Hello " + user.getDisplayName() + ",\n\n"
                        + "Your specialist registration request was reviewed by the administrator and was not approved at this time.\n"
                        + "If you believe this was a mistake, please contact the system administrator for further support."
        );
    }

    private void sendEmail(String to, String subject, String text) {
        if (!mailEnabled) {
            log.info("Mail disabled. Skipping email to {} with subject '{}'.", to, subject);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("Mail is enabled but JavaMailSender is not configured.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
