package com.cpt202.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import com.cpt202.booking.service.UserService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.DisabledException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Allow static assets
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/avatars/**", "/vendors/**", "/webjars/**").permitAll()
                // Public pages
                .requestMatchers("/", "/auth/login", "/auth/register", "/auth/forgot-password", "/auth/reset-password", "/auth/verify-email", "/auth/resend-verification", "/error", "/error/**").permitAll()
                // Role-based access control
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/specialist/**").hasRole("SPECIALIST")
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")          // Custom login page
                .loginProcessingUrl("/auth/login") // Login processing URL
                .defaultSuccessUrl("/auth/success", true)
                .failureHandler((request, response, exception) ->
                        response.sendRedirect(buildFailureRedirect(request.getParameter("username"), exception)))
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            )
            ;
        return http.build();
    }

    private String buildFailureRedirect(String username, AuthenticationException exception) {
        if (exception instanceof DisabledException) {
            String reason = userService.resolveLoginBlockReason(username);
            if (reason != null) {
                return "/auth/login?" + reason + "=true";
            }
        }
        return "/auth/login?error=true";
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
