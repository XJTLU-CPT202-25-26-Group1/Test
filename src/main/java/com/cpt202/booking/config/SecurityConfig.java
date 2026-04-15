package com.cpt202.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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

    // ⚠️ 临时测试用户（生产环境替换为数据库认证！）
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123") // {noop}表示明文（仅测试！）
                .roles("ADMIN")
                .build();
        
        UserDetails specialist = User.withUsername("specialist")
                .password("{noop}specialist123")
                .roles("SPECIALIST")
                .build();
        
        UserDetails customer = User.withUsername("customer")
                .password("{noop}customer123")
                .roles("CUSTOMER")
                .build();
        
        return new InMemoryUserDetailsManager(admin, specialist, customer);
    }
}
