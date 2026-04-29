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

    private static final String DEFAULT_BASE_URL = "http://47.97.155.89:8080";
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean mailEnabled;
    private final String fromAddress;
    private final String baseUrl;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${app.mail.enabled:false}") boolean mailEnabled,
                        @Value("${app.mail.from:no-reply@example.com}") String fromAddress,
                        @Value("${app.base-url:" + DEFAULT_BASE_URL + "}") String baseUrl) {
        this.mailSenderProvider = mailSenderProvider;
        this.mailEnabled = mailEnabled;
        this.fromAddress = fromAddress;
        this.baseUrl = normalizeHttpBaseUrl(baseUrl);
    }

    public void sendVerificationEmail(User user) {
        String verifyLink = baseUrl + "/auth/verify-email?username=" + user.getUsername() + "&token=" + user.getVerificationToken();
        sendEmail(
                user.getEmail(),
                "Verify your XJTLU Academic Expert Appointment account",
                "Hello " + user.getDisplayName() + ",\n\n"
                        + "Please verify your account email for the XJTLU Academic Expert Appointment System by opening the link below:\n"
                        + verifyLink + "\n\n"
                        + "If you did not create this account, you can ignore this message."
        );
    }

    public void sendResetPasswordEmail(User user) {
        String resetLink = baseUrl + "/auth/reset-password?username=" + user.getUsername() + "&token=" + user.getResetToken();
        sendEmail(
                user.getEmail(),
                "Reset your XJTLU Academic Expert Appointment password",
                "Hello " + user.getDisplayName() + ",\n\n"
                        + "Use the link below to reset your XJTLU appointment system password:\n"
                        + resetLink + "\n\n"
                        + "Reset token: " + user.getResetToken() + "\n\n"
                        + "If you did not request a password reset, you can ignore this message."
        );
    }

    public void sendSpecialistApprovalEmail(User user, boolean emailVerified) {
        sendEmail(
                user.getEmail(),
                "Your XJTLU expert registration has been approved",
                "Hello " + user.getDisplayName() + ",\n\n"
                        + "Your XJTLU academic expert registration has been approved by the administrator.\n"
                        + (emailVerified
                        ? "You can now log in and start managing consultation availability."
                        : "Please verify your email first. After email verification, you will be able to log in.")
                        + "\n\n"
                        + "Login page: " + baseUrl + "/auth/login"
        );
    }

    public void sendSpecialistRejectionEmail(User user) {
        sendEmail(
                user.getEmail(),
                "Your XJTLU expert registration was not approved",
                "Hello " + user.getDisplayName() + ",\n\n"
                        + "Your XJTLU academic expert registration request was reviewed by the administrator and was not approved at this time.\n"
                        + "If you believe this was a mistake, please contact the module administrator or system administrator for further support."
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

    private String normalizeHttpBaseUrl(String configuredBaseUrl) {
        String normalized = configuredBaseUrl == null || configuredBaseUrl.isBlank()
                ? DEFAULT_BASE_URL
                : configuredBaseUrl.trim();
        if (normalized.startsWith("https://")) {
            normalized = "http://" + normalized.substring("https://".length());
        } else if (!normalized.startsWith("http://")) {
            normalized = "http://" + normalized;
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
