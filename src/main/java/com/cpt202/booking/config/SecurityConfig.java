package com.cpt202.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // ✅ 静态资源放行（关键！）
                .requestMatchers("/css/**", "/js/**", "/images/**", "/vendors/**", "/webjars/**").permitAll()
                // 公开页面
                .requestMatchers("/", "/auth/login", "/auth/register", "/auth/forgot-password", "/auth/reset-password", "/error/**").permitAll()
                // 角色权限
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/specialist/**").hasAnyRole("SPECIALIST", "ADMIN")
                .requestMatchers("/customer/**").hasAnyRole("CUSTOMER", "ADMIN")
                // 其他请求需认证
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")          // 自定义登录页
                .loginProcessingUrl("/auth/login") // 处理登录的URL
                .defaultSuccessUrl("/auth/success", true)
                .failureUrl("/auth/login?error=true")
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
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // 如有API接口
            );
        return http.build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
