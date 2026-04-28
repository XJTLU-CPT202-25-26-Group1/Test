package com.cpt202.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AvatarWebConfig implements WebMvcConfigurer {

    private final Path avatarUploadDirectory;

    public AvatarWebConfig(@Value("${app.avatar.upload-dir:uploads/avatars}") String avatarUploadDirectory) {
        this.avatarUploadDirectory = Paths.get(avatarUploadDirectory).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = avatarUploadDirectory.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations(location);
    }
}
